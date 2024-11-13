package org.example.kafka.dto;

import lombok.Data;

@Data
public class Payment {
    private String paymentId;
    private String orderId;
    private String transactionId;
    private String status;
    private double amount;
}
