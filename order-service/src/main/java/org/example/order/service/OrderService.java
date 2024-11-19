package org.example.order.service;

import org.springframework.stereotype.Service;
import org.example.order.model.Order;
import org.example.order.model.OrderItem;
import org.example.order.repository.OrderRepository;
import org.example.order.repository.OrderItemRepository;
import org.example.order.dto.OrderRequest;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final DiscountClient discountClient;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductClient productClient, DiscountClient discountClient){
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productClient = productClient;
        this.discountClient = discountClient;
    }

//    @Transactional
//    public Mono<Order> createOrder(OrderRequest orderRequest){
//        // Fetch product details
//        List<Mono<ProductClient.ProductResponse>> productMonos = orderRequest.getItems().stream()
//                .map(item -> productClient.getProductById(item.getProductId()))
//                .collect(Collectors.toList());
//
//        return Flux.merge(productMonos)
//                .collectList()
//                .flatMap(products -> {
//                    // Calculate total amount
//                    Double totalAmount = 0.0;
//                    for(int i=0; i < products.size(); i++){
//                        ProductClient.ProductResponse product = products.get(i);
//                        OrderRequest.OrderItemRequest itemRequest = orderRequest.getItems().get(i);
//                        totalAmount += product.getPrice() * itemRequest.getQuantity();
//                    }
//
//                    // Apply discount if applicable
//                    Mono<Double> discountAmountMono = Mono.just(0.0);
//                    if(orderRequest.getDiscountCode() != null && !orderRequest.getDiscountCode().isEmpty()){
//                        Double finalTotalAmount1 = totalAmount;
//                        discountAmountMono = discountClient.getDiscountByCode(orderRequest.getDiscountCode())
//                                .map(discount -> finalTotalAmount1 * discount.getPercentage() / 100)
//                                .defaultIfEmpty(0.0);
//                    }
//
//                    Double finalTotalAmount = totalAmount;
//                    return discountAmountMono.flatMap(discountAmount -> {
//                        Double finalAmount = finalTotalAmount - discountAmount;
//                        Order order = new Order();
//                        order.setCustomerName(orderRequest.getCustomerName());
//                        order.setTotalAmount(finalTotalAmount);
//                        order.setDiscountCode(orderRequest.getDiscountCode());
//                        order.setDiscountAmount(discountAmount);
//                        order.setFinalAmount(finalAmount);
//                        order.setStatus("CREATED");
//
//                        return orderRepository.save(order)
//                                .flatMap(savedOrder -> {
//                                    List<OrderItem> orderItems = orderRequest.getItems().stream()
//                                            .map(itemRequest -> {
//                                                OrderItem orderItem = new OrderItem();
//                                                orderItem.setOrderId(savedOrder.getId());
//                                                orderItem.setProductId(itemRequest.getProductId());
//                                                orderItem.setQuantity(itemRequest.getQuantity());
//                                                // Assuming price is fetched from product
//                                                ProductClient.ProductResponse product = products.stream()
//                                                        .filter(p -> p.getId().equals(itemRequest.getProductId()))
//                                                        .findFirst().orElse(null);
//                                                orderItem.setPrice(product != null ? product.getPrice() : 0.0);
//                                                return orderItem;
//                                            }).collect(Collectors.toList());
//
//                                    return orderItemRepository.saveAll(orderItems)
//                                            .then(Mono.just(savedOrder));
//                                });
//                    });
//                })
//                .onErrorResume(e -> {
//                    // Handle errors, possibly mark order as FAILED
//                    Order failedOrder = new Order();
//                    failedOrder.setCustomerName(orderRequest.getCustomerName());
//                    failedOrder.setStatus("FAILED");
//                    return orderRepository.save(failedOrder);
//                });
//    }

    @Transactional
    public Mono<Order> createOrder(OrderRequest orderRequest) {
        // Fetch product details concurrently for all requested products
        List<Mono<ProductClient.ProductResponse>> productMonos = orderRequest.getItems().stream()
                .map(item -> productClient.getProductById(item.getProductId()))
                .collect(Collectors.toList());

        return Flux.merge(productMonos)
                .collectList()
                .flatMap(products -> {
                    // Calculate total amount based on fetched product details
                    double totalAmount = 0.0;
                    for (int i = 0; i < products.size(); i++) {
                        ProductClient.ProductResponse product = products.get(i);
                        OrderRequest.OrderItemRequest itemRequest = orderRequest.getItems().get(i);
                        totalAmount += product.getPrice() * itemRequest.getQuantity();
                    }

                    // Apply discount if a discount code is provided
                    Mono<Double> discountAmountMono = Mono.just(0.0);
                    if (orderRequest.getDiscountCode() != null && !orderRequest.getDiscountCode().isEmpty()) {
                        double finalTotalAmount = totalAmount;
                        discountAmountMono = discountClient.getDiscountByCode(orderRequest.getDiscountCode())
                                .map(discount -> finalTotalAmount * discount.getPercentage() / 100)
                                .defaultIfEmpty(0.0);
                    }

                    final double calculatedTotalAmount = totalAmount;
                    return discountAmountMono.flatMap(discountAmount -> {
                        double finalAmount = calculatedTotalAmount - discountAmount;

                        // Create the Order entity
                        Order order = new Order();
                        order.setCustomerName(orderRequest.getCustomerName());
                        order.setTotalAmount(calculatedTotalAmount);
                        order.setDiscountCode(orderRequest.getDiscountCode());
                        order.setDiscountAmount(discountAmount);
                        order.setFinalAmount(finalAmount);
                        order.setStatus("CREATED");

                        return orderRepository.save(order)
                                .flatMap(savedOrder -> {
                                    // Create and save OrderItem entities
                                    List<OrderItem> orderItems = orderRequest.getItems().stream()
                                            .map(itemRequest -> {
                                                ProductClient.ProductResponse product = products.stream()
                                                        .filter(p -> p.getId().equals(itemRequest.getProductId()))
                                                        .findFirst()
                                                        .orElseThrow(() -> new IllegalStateException("Product not found"));

                                                OrderItem orderItem = new OrderItem();
                                                orderItem.setOrderId(savedOrder.getId());
                                                orderItem.setProductId(itemRequest.getProductId());
                                                orderItem.setQuantity(itemRequest.getQuantity());
                                                orderItem.setPrice(product.getPrice());
                                                return orderItem;
                                            })
                                            .collect(Collectors.toList());

                                    return orderItemRepository.saveAll(orderItems)
                                            .then(Mono.just(savedOrder));
                                });
                    });
                })
                .onErrorResume(e -> {
                    // Compensate by creating a failed order record
                    Order failedOrder = new Order();
                    failedOrder.setCustomerName(orderRequest.getCustomerName());
                    failedOrder.setStatus("FAILED");
                    return orderRepository.save(failedOrder);
                });
    }

    // Additional methods like getOrderById, getAllOrders etc.
}
