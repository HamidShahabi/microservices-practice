package org.example.product.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("t_products")
public class Product {
    @Id
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
