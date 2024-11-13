// src/main/java/com/example/order/entity/Order.java
package org.example.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    private String id;
    private String productId;
    private int quantity;
    private String status; // PENDING, COMPLETED, FAILED
    private String discountCode;
    private double finalAmount;
    private String transactionId;
}
