package org.example.discount.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.example.discount.model.Discount;
import reactor.core.publisher.Mono;

@Repository
public interface DiscountRepository extends ReactiveCrudRepository<Discount, Long> {
    Mono<Discount> findByCode(String code);
}
