package org.example.discount.service;

import org.springframework.stereotype.Service;
import org.example.discount.model.Discount;
import org.example.discount.repository.DiscountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository){
        this.discountRepository = discountRepository;
    }

    public Flux<Discount> getAllDiscounts(){
        return discountRepository.findAll();
    }

    public Mono<Discount> getDiscountById(Long id){
        return discountRepository.findById(id);
    }

    public Mono<Discount> getDiscountByCode(String code){
        return discountRepository.findByCode(code);
    }

    public Mono<Discount> createDiscount(Discount discount){
        return discountRepository.save(discount);
    }

    public Mono<Discount> updateDiscount(Long id, Discount discount){
        return discountRepository.findById(id)
                .flatMap(existingDiscount -> {
                    existingDiscount.setCode(discount.getCode());
                    existingDiscount.setPercentage(discount.getPercentage());
                    existingDiscount.setDescription(discount.getDescription());
                    return discountRepository.save(existingDiscount);
                });
    }

    public Mono<Void> deleteDiscount(Long id){
        return discountRepository.deleteById(id);
    }
}
