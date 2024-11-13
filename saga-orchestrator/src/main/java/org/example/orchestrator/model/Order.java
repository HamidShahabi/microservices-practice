package org.example.orchestrator.model;

import lombok.Data;

@Data
public class Order {
    private String id;
    private String productId;
    private int quantity;
    private String status;
    private String discountCode;
    private double finalAmount;
}
