package org.example.orchestrator.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.example.kafka.dto.EventMessage;
import org.example.orchestrator.service.SagaOrchestratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.orchestrator.service.SagaOrchestratorServiceAsync;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrchestratorEventListener {

    private final SagaOrchestratorServiceAsync orchestratorService;
    private final ObjectMapper objectMapper;

    public OrchestratorEventListener(SagaOrchestratorServiceAsync orchestratorService) {
        this.orchestratorService = orchestratorService;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @KafkaListener(topics = "order-event-topic", groupId = "orchestrator-group")
    public void onOrderEvent(String message) {
        processEvent(message);
    }

    @KafkaListener(topics = "discount-event-topic", groupId = "orchestrator-group")
    public void onDiscountEvent(String message) {
        processEvent(message);
    }

    @KafkaListener(topics = "payment-event-topic", groupId = "orchestrator-group")
    public void onPaymentEvent(String message) {
        processEvent(message);
    }

    @KafkaListener(topics = "inventory-event-topic", groupId = "orchestrator-group")
    public void onInventoryEvent(String message) {
        processEvent(message);
    }

    private void processEvent(String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);
            orchestratorService.handleEvent(eventMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

