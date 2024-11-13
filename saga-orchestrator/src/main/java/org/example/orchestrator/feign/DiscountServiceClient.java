package org.example.orchestrator.feign;

import org.example.orchestrator.model.Discount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "discount-service", url = "http://localhost:8084")
@Component
public interface DiscountServiceClient {

    @GetMapping("/discounts/{discountCode}")
    Discount getDiscount(@PathVariable String discountCode);

    @GetMapping("/discounts/{discountCode}/isActive")
    boolean isDiscountActive(@PathVariable String discountCode);
}
