package org.example.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    private String paymentId;
    private String orderId;
    private String status; // PENDING, COMPLETED, REFUNDED
    private double amount;
}

