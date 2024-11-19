package org.example.product.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.example.product.model.Product;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    // Additional query methods if needed
}
