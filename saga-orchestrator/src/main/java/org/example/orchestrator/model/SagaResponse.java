package org.example.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SagaResponse {
    private String orderId;
    private String paymentId;
    private String message;
}

