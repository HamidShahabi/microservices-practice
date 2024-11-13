package org.example.orchestrator.model;

import lombok.Data;

@Data
public class Discount {
    private String discountCode;
    private double percentage;
    private boolean active;
}
