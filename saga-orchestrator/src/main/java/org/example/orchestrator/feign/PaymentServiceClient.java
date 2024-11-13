package org.example.orchestrator.feign;

import org.example.orchestrator.model.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "http://localhost:8082")
@Component
public interface PaymentServiceClient {

    @PostMapping("/payments")
    Payment processPayment(@RequestParam String orderId, @RequestParam double amount);

    @PostMapping("/payments/{paymentId}/refund")
    void refundPayment(@PathVariable String paymentId);

    @GetMapping("/payments/{paymentId}")
    Payment getPaymentById(@PathVariable String paymentId);
}
