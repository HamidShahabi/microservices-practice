package org.example.orchestrator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.dto.*;
import org.example.orchestrator.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
@Service
@Slf4j
public class SagaOrchestratorServiceAsync {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SagaOrchestratorServiceAsync(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String initiateOrderSaga(OrderRequestDto orderRequest) {
//        String transactionId = orderRequest.getTransactionId();
        String transactionId = UUID.randomUUID().toString();
        orderRequest.setTransactionId(transactionId);
        try {
            sendCommand("order-command-topic", transactionId, "CREATE_ORDER", orderRequest);
        } catch (Exception e) {
            log.error("Failed to initiate Order Saga for transactionId: {}", transactionId, e);
        }
        return "Order Creation Initiated";
    }

    private void sendCommand(String topic, String transactionId, String commandType, Object payload) throws Exception {
        String message = objectMapper.writeValueAsString(new CommandMessage(transactionId, commandType, objectMapper.writeValueAsString(payload)));
        kafkaTemplate.send(topic, transactionId, message);
        log.info("Sent command [{}] to topic [{}] for transactionId: {}", commandType, topic, transactionId);
    }

    public void handleEvent(EventMessage eventMessage) {
        try {
            String transactionId = eventMessage.getTransactionId();
            String eventType = eventMessage.getEventType();

            switch (eventType) {
                case "ORDER_CREATED":
                    handleDiscountStep(transactionId, eventMessage.getPayload());
                    break;
                case "DISCOUNT_APPLIED":
                    handlePaymentStep(transactionId, eventMessage.getPayload());
                    break;
                case "PAYMENT_COMPLETED":
                    handleInventoryStep(transactionId, eventMessage.getPayload());
                    break;
                case "INVENTORY_REDUCED":
                    finalizeOrder(transactionId, eventMessage.getPayload());
                    break;
                case "ERROR":
                    log.error("Error event received for transactionId: {}", transactionId);
                    handleCompensation(transactionId);
                    break;
                default:
                    log.warn("Unknown event type received: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error handling event: {}", eventMessage, e);
        }
    }

    private void handleDiscountStep(String transactionId, String payload) throws Exception {
        OrderRequestDto orderRequest = objectMapper.readValue(payload, OrderRequestDto.class);
//        DiscountRequestDto discountRequest = new DiscountRequestDto(transactionId, orderRequest.getDiscountCode(), 100.0);
        sendCommand("discount-command-topic", transactionId, "APPLY_DISCOUNT", orderRequest);
    }

    private void handlePaymentStep(String transactionId, String payload) throws Exception {
        OrderRequestDto orderRequest = objectMapper.readValue(payload, OrderRequestDto.class);
        PaymentRequestDto paymentRequest = new PaymentRequestDto(transactionId, orderRequest.getProductId(), orderRequest.getQuantity() * 20.0);
        sendCommand("payment-command-topic", transactionId, "PROCESS_PAYMENT", paymentRequest);
    }

    private void handleInventoryStep(String transactionId, String payload) throws Exception {
        InventoryRequestDto inventoryRequest = new InventoryRequestDto(transactionId, "P100", 1);
        sendCommand("inventory-command-topic", transactionId, "REDUCE_INVENTORY", inventoryRequest);
    }

    private void finalizeOrder(String transactionId, String payload) throws Exception {
        log.info("Finalizing order for transactionId: {}", transactionId);
    }

    private void handleCompensation(String transactionId) {
        log.info("Compensating transactionId: {}", transactionId);
        // Send reverse commands to all topics
    }
}
