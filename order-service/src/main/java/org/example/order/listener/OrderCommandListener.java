package org.example.order.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.example.kafka.dto.CommandMessage;
import org.example.kafka.dto.EventMessage;
import org.example.kafka.dto.OrderRequestDto;
import org.example.order.entity.Order;
import org.example.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCommandListener {

    private final OrderService orderService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderCommandListener(OrderService orderService, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "order-command-topic", groupId = "order-service-group")
    public void onCommand(String message) {
        try {
            CommandMessage commandMessage = objectMapper.readValue(message, CommandMessage.class);
            String transactionId = commandMessage.getTransactionId();
            String commandType = commandMessage.getCommandType();

            if ("CREATE_ORDER".equals(commandType)) {
                OrderRequestDto orderRequest = objectMapper.readValue(commandMessage.getPayload(), OrderRequestDto.class);
                Order order = orderService.createOrder(orderRequest);
                publishEvent("order-event-topic", transactionId, "ORDER_CREATED", order);
            } else if ("REVERSE_ORDER".equals(commandType)) {
//                orderService.reverseOrder(transactionId);
                publishEvent("order-event-topic", transactionId, "ORDER_REVERSED", null);
            } else {
                log.warn("Unknown command type received: {}", commandType);
            }
        } catch (Exception e) {
            log.error("Error processing command message: {}", message, e);
            publishErrorEvent("order-event-topic", e.getMessage());
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


