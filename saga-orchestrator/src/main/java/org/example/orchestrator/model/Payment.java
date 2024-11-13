package org.example.orchestrator.model;

import lombok.Data;

@Data
public class Payment {
    private String paymentId;
    private String orderId;
    private String status; // PENDING, COMPLETED, REFUNDED
    private double amount;
}
