package org.example.discount.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "discounts")
@Data
public class Discount {
    @Id
    private String discountCode;
    private double percentage; // e.g., 10.0 for 10% discount
    private boolean active;
}

