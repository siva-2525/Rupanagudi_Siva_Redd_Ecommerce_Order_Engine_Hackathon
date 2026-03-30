package com.ecommerce.services;

import java.util.Random;

public class PaymentService {
    private final Random random = new Random();

    public boolean processPayment(String orderId, double amount) {
        System.out.println("💳 Processing payment of ₹" + amount + " for order " + orderId + "...");
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        boolean isSuccess = random.nextInt(100) < 80;
        if (isSuccess) System.out.println("✅ Payment Successful!");
        else System.out.println("❌ Payment Failed (Simulated Gateway Error).");
        
        return isSuccess;
    }
}