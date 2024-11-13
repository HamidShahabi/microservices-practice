package org.example.order.controller;

import org.example.kafka.dto.OrderRequestDto;
import org.example.order.entity.Order;
import org.example.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequestDto order) {
        return service.createOrder(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        service.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated to " + status);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String orderId) {
        return service.getOrderById(orderId);
    }
}

