package org.example.kafka.dto;

import lombok.Data;

@Data
public class Order {
    private String id;
    private String transactionId;
    private String productId;
    private int quantity;
    private String status;
    private String discountCode;
    private double finalAmount;
}
