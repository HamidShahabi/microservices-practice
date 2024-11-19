package org.example.discount.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("t_discounts")
public class Discount {
    @Id
    private Long id;
    private String code;
    private Double percentage;
    private String description;
}
