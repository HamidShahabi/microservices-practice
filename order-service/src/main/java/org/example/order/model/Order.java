package org.example.order.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Table("t_orders")
public class Order {
    @Id
    private Long id;
    private String customerName;
    private Double totalAmount;
    private String discountCode;
    private Double discountAmount;
    private Double finalAmount;
    private String status; // e.g., CREATED, FAILED

    // Not mapped, handled separately
}
