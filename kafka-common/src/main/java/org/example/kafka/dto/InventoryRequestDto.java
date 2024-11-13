package org.example.kafka.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestDto {
    private String transactionId;
    private String productId;
    private int quantity;
}
