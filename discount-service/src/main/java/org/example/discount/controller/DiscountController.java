package org.example.discount.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.example.discount.model.Discount;
import org.example.discount.service.DiscountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    private final DiscountService discountService;

    public DiscountController(DiscountService discountService){
        this.discountService = discountService;
    }

    @GetMapping
    public Flux<Discount> getAllDiscounts(){
        return discountService.getAllDiscounts();
    }

    @GetMapping("/{id}")
    public Mono<Discount> getDiscountById(@PathVariable Long id){
        return discountService.getDiscountById(id);
    }

    @GetMapping("/code/{code}")
    public Mono<Discount> getDiscountByCode(@PathVariable String code){
        return discountService.getDiscountByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Discount> createDiscount(@Valid @RequestBody Discount discount){
        return discountService.createDiscount(discount);
    }

    @PutMapping("/{id}")
    public Mono<Discount> updateDiscount(@PathVariable Long id, @Valid @RequestBody Discount discount){
        return discountService.updateDiscount(id, discount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDiscount(@PathVariable Long id){
        return discountService.deleteDiscount(id);
    }
}
