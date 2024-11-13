package org.example.discount.service;

import org.example.discount.entity.Discount;
import org.example.discount.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountRepository repository;

    public DiscountService(DiscountRepository repository) {
        this.repository = repository;
    }

    public Discount getDiscount(String discountCode) {
        return repository.findById(discountCode)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public boolean isDiscountActive(String discountCode) {
        Discount discount = getDiscount(discountCode);
        return discount.isActive();
    }

    public Discount addDiscount(Discount discount) {
        return repository.save(discount);
    }

    public void deactivateDiscount(String discountCode) {
        Discount discount = getDiscount(discountCode);
        discount.setActive(false);
        repository.save(discount);
    }
}

