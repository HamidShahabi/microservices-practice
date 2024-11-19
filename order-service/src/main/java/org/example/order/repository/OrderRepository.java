package org.example.order.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.example.order.model.Order;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    // Additional query methods if needed
}
