package com.life_insurance_system.service;

import com.life_insurance_system.model.Payment;
import com.life_insurance_system.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByPolicy(com.life_insurance_system.model.Policy policy) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getPolicy().getPolicyId() == policy.getPolicyId())
                .collect(java.util.stream.Collectors.toList());
    }
}