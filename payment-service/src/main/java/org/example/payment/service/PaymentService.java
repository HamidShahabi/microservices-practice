package org.example.payment.service;

import org.example.payment.entity.Payment;
import org.example.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment processPayment(String orderId, double amount) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("COMPLETED"); // Simulate successful payment
        return repository.save(payment);
    }

    public void refundPayment(String paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("REFUNDED");
        repository.save(payment);
    }

    public Payment getPaymentById(String paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}

