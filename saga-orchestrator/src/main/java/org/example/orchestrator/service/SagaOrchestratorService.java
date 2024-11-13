package org.example.orchestrator.service;

import org.example.orchestrator.model.Order;
import org.example.orchestrator.model.SagaResponse;

public interface SagaOrchestratorService {
    SagaResponse createOrderSaga(Order orderRequest);

    }
