package org.example.orchestrator.feign;

import org.example.orchestrator.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "http://localhost:8081")
@Component
public interface OrderServiceClient {

    @PostMapping("/orders")
    Order createOrder(@RequestBody Order order);

    @PutMapping("/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable String orderId, @RequestParam String status);

    @GetMapping("/orders/{orderId}")
    Order getOrderById(@PathVariable String orderId);
}
