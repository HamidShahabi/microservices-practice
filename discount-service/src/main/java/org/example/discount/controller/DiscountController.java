package org.example.discount.controller;

import org.example.discount.entity.Discount;
import org.example.discount.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @GetMapping("/{discountCode}")
    public Discount getDiscount(@PathVariable String discountCode) {
        return service.getDiscount(discountCode);
    }

    @GetMapping("/{discountCode}/isActive")
    public boolean isDiscountActive(@PathVariable String discountCode) {
        return service.isDiscountActive(discountCode);
    }

    @PostMapping
    public Discount addDiscount(@RequestBody Discount discount) {
        return service.addDiscount(discount);
    }

    @PutMapping("/{discountCode}/deactivate")
    public ResponseEntity<String> deactivateDiscount(@PathVariable String discountCode) {
        service.deactivateDiscount(discountCode);
        return ResponseEntity.ok("Discount deactivated successfully");
    }
}
