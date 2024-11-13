package org.example.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String transactionId;
    private String productId;
    private int quantity;
    private String discountCode;
    private double amount;
}

