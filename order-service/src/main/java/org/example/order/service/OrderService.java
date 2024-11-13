package org.example.order.service;

import lombok.extern.slf4j.Slf4j;
import org.example.order.entity.Order;
import org.example.order.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

//    public OrderService(OrderRepository repository) {
//        this.repository = repository;
//    }

//    public Order createOrder(Order order) {
//        order.setId(UUID.randomUUID().toString());
//        order.setStatus("PENDING");
//        return repository.save(order);
//    }
//
//    public void updateOrderStatus(String orderId, String status) {
//        Order order = repository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//        order.setStatus(status);
//        repository.save(order);
//    }

    public OrderService(OrderRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order createOrder(Order order) {
        order.setId(UUID.randomUUID().toString());
        order.setStatus("PENDING");
        Order savedOrder = repository.save(order);

        String message = "Order created: " + savedOrder.getId();
        log.info("Publishing to Kafka: {}", message);
        kafkaTemplate.send("order-topic", "Order created: " + savedOrder.getId());
        return savedOrder;
    }

    public void updateOrderStatus(String orderId, String status) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        repository.save(order);

        String message = "Order updated: " + orderId + " to status " + status;
        log.info("Publishing to Kafka: {}", message);
        kafkaTemplate.send("order-topic", message);
    }
    public Order getOrderById(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}

