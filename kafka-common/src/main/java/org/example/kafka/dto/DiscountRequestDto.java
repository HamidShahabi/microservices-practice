package org.example.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequestDto {
    private String transactionId;
    private String discountCode;
    private double originalAmount;
}

