package org.example.orchestrator.service;

import org.example.orchestrator.feign.DiscountServiceClient;
import org.example.orchestrator.feign.InventoryServiceClient;
import org.example.orchestrator.feign.OrderServiceClient;
import org.example.orchestrator.feign.PaymentServiceClient;
import org.example.orchestrator.model.Discount;
import org.example.orchestrator.model.Order;
import org.example.orchestrator.model.Payment;
import org.example.orchestrator.model.SagaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("sync")
public class SagaOrchestratorServiceSync implements SagaOrchestratorService{

    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final DiscountServiceClient discountServiceClient;

    public SagaOrchestratorServiceSync(OrderServiceClient orderServiceClient,
                                       PaymentServiceClient paymentServiceClient,
                                       InventoryServiceClient inventoryServiceClient,
                                       DiscountServiceClient discountServiceClient) {
        this.orderServiceClient = orderServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.discountServiceClient = discountServiceClient;
    }

    @Override
    public SagaResponse createOrderSaga(Order orderRequest) {
        Order order = null;
        Payment payment = null;
        boolean inventoryReduced = false;

        try {
            // Step 1: Create Order
            order = orderServiceClient.createOrder(orderRequest);

            // Step 2: Apply Discount (if any)
            double amount = calculateAmount(order);
            if (order.getDiscountCode() != null && !order.getDiscountCode().isEmpty()) {
                Discount discount = discountServiceClient.getDiscount(order.getDiscountCode());
                if (discount.isActive()) {
                    amount = amount - (amount * discount.getPercentage() / 100);
                } else {
                    throw new RuntimeException("Discount code is not active");
                }
            }
            order.setFinalAmount(amount);

            // Step 3: Process Payment
            payment = paymentServiceClient.processPayment(order.getId(), amount);

            // Step 4: Reduce Inventory
            ResponseEntity<String> inventoryResponse = inventoryServiceClient.reduceStock(
                    order.getProductId(), order.getQuantity());

            if (inventoryResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Inventory reduction failed");
            } else {
                inventoryReduced = true;
            }

            // Step 5: Update Order Status to COMPLETED
            orderServiceClient.updateOrderStatus(order.getId(), "COMPLETED");

            // Return Order and Payment details
            return new SagaResponse(order.getId(), payment.getPaymentId(), "Order processed successfully");

        } catch (Exception e) {
            // Compensation logic
            if (inventoryReduced) {
                inventoryServiceClient.restoreStock(order.getProductId(), order.getQuantity());
            }
            if (payment != null) {
                paymentServiceClient.refundPayment(payment.getPaymentId());
            }
            if (order != null) {
                orderServiceClient.updateOrderStatus(order.getId(), "FAILED");
            }

            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }

    private double calculateAmount(Order order) {
        // For simplicity, assume each item costs $20
        return order.getQuantity() * 20.0;
    }
}
