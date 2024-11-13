package org.example.discount.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.example.kafka.dto.CommandMessage;
import org.example.kafka.dto.DiscountRequestDto;
import org.example.kafka.dto.EventMessage;
import org.example.discount.entity.Discount;
import org.example.discount.service.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.dto.OrderRequestDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscountCommandListener {

    private final DiscountService discountService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DiscountCommandListener(DiscountService discountService, KafkaTemplate<String, String> kafkaTemplate) {
        this.discountService = discountService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "discount-command-topic", groupId = "discount-service-group")
    public void onCommand(String message) {
        try {
            CommandMessage commandMessage = objectMapper.readValue(message, CommandMessage.class);
            String transactionId = commandMessage.getTransactionId();
            String commandType = commandMessage.getCommandType();

            if ("APPLY_DISCOUNT".equals(commandType)) {
                OrderRequestDto discountRequest = objectMapper.readValue(commandMessage.getPayload(), OrderRequestDto.class);
                double discountedAmount = discountService.applyDiscount(new DiscountRequestDto(null, discountRequest.getDiscountCode(), discountRequest.getAmount()));
                discountRequest.setAmount(discountedAmount);
                publishEvent("discount-event-topic", transactionId, "DISCOUNT_APPLIED", discountRequest);
            } else if ("REVERSE_DISCOUNT".equals(commandType)) {
//                DiscountRequestDto discountRequest = objectMapper.readValue(commandMessage.getPayload(), DiscountRequestDto.class);
//                discountService.revertDiscount(discountRequest);
                publishEvent("discount-event-topic", transactionId, "DISCOUNT_REVERSED", null);
            } else {
                log.warn("Unknown command type received: {}", commandType);
            }
        } catch (Exception e) {
            log.error("Error processing discount command: {}", message, e);
            publishErrorEvent("discount-event-topic", e.getMessage());
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
