package org.example.order.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("t_order_items")
public class OrderItem {
    @Id
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double price;
}
