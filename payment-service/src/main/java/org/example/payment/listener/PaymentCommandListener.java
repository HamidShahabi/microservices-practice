package org.example.payment.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.example.kafka.dto.CommandMessage;
import org.example.kafka.dto.EventMessage;
import org.example.kafka.dto.PaymentRequestDto;
import org.example.payment.entity.Payment;
import org.example.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentCommandListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentCommandListener(PaymentService paymentService, KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "payment-command-topic", groupId = "payment-service-group")
    public void onCommand(String message) {
        try {
            CommandMessage commandMessage = objectMapper.readValue(message, CommandMessage.class);
            String transactionId = commandMessage.getTransactionId();
            String commandType = commandMessage.getCommandType();

            if ("PROCESS_PAYMENT".equals(commandType)) {
                PaymentRequestDto paymentRequest = objectMapper.readValue(commandMessage.getPayload(), PaymentRequestDto.class);
                Payment payment = paymentService.processPayment(paymentRequest.getOrderId(), paymentRequest.getAmount());
                publishEvent("payment-event-topic", transactionId, "PAYMENT_COMPLETED", payment);
            } else if ("REVERSE_PAYMENT".equals(commandType)) {
//                paymentService.reversePayment(transactionId);
                publishEvent("payment-event-topic", transactionId, "PAYMENT_REVERSED", null);
            } else {
                log.warn("Unknown command type received: {}", commandType);
            }
        } catch (Exception e) {
            log.error("Error processing payment command: {}", message, e);
            publishErrorEvent("payment-event-topic", e.getMessage());
        }
    }

    private void publishEvent(String topic, String transactionId, String eventType, Object payload) {
        try {
            String payloadString = payload != null ? objectMapper.writeValueAsString(payload) : null;
            EventMessage eventMessage = new EventMessage(transactionId, eventType, payloadString);
            kafkaTemplate.send(topic, transactionId, objectMapper.writeValueAsString(eventMessage));
            log.info("Published event: {} for transactionId: {}", eventType, transactionId);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", eventType, e);
        }
    }

    private void publishErrorEvent(String topic, String errorMessage) {
        try {
            EventMessage eventMessage = new EventMessage(null, "ERROR", errorMessage);
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(eventMessage));
            log.error("Published error event: {}", errorMessage);
        } catch (Exception e) {
            log.error("Failed to publish error event: {}", errorMessage, e);
        }
    }
}

