package org.example.inventory.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.example.kafka.dto.CommandMessage;
import org.example.kafka.dto.EventMessage;
import org.example.kafka.dto.InventoryRequestDto;
import org.example.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryCommandListener {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InventoryCommandListener(InventoryService inventoryService, KafkaTemplate<String, String> kafkaTemplate) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "inventory-command-topic", groupId = "inventory-service-group")
    public void onCommand(String message) {
        try {
            CommandMessage commandMessage = objectMapper.readValue(message, CommandMessage.class);
            String transactionId = commandMessage.getTransactionId();
            String commandType = commandMessage.getCommandType();

            if ("REDUCE_INVENTORY".equals(commandType)) {
                InventoryRequestDto inventoryRequest = objectMapper.readValue(commandMessage.getPayload(), InventoryRequestDto.class);
                inventoryService.reduceStock(inventoryRequest.getProductId(), inventoryRequest.getQuantity());
                publishEvent("inventory-event-topic", transactionId, "INVENTORY_REDUCED", inventoryRequest);
            } else if ("RESTORE_INVENTORY".equals(commandType)) {
//                inventoryService.restoreStock(commandMessage.getTransactionId());
                publishEvent("inventory-event-topic", transactionId, "INVENTORY_RESTORED", null);
            } else {
                log.warn("Unknown command type: {}", commandType);
            }
        } catch (Exception e) {
            log.error("Error processing inventory command: {}", message, e);
            publishErrorEvent("inventory-event-topic", e.getMessage());
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
