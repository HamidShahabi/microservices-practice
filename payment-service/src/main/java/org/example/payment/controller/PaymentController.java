package org.example.payment.controller;

import org.example.payment.entity.Payment;
import org.example.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Payment processPayment(@RequestParam String orderId, @RequestParam double amount) {
        return service.processPayment(orderId, amount);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable String paymentId) {
        service.refundPayment(paymentId);
        return ResponseEntity.ok("Payment refunded successfully");
    }

    @GetMapping("/{paymentId}")
    public Payment getPaymentById(@PathVariable String paymentId) {
        return service.getPaymentById(paymentId);
    }
}
