package org.atnt.ticketbooking.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public boolean processPayment(String userId, double amount) {
        // Simplified payment gateway integration
        return true;
    }
}