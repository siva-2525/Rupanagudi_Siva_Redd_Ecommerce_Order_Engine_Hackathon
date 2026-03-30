package com.ecommerce.engine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Queue;
import java.util.LinkedList;

public class FraudDetectionSystem {
    private final ConcurrentHashMap<String, Queue<Long>> userOrderTimestamps = new ConcurrentHashMap<>();

    public boolean isFraudulent(String userId, double orderTotal) {
        if (orderTotal > 50000) {
            System.out.println("🚨 FRAUD ALERT: High-value order detected for " + userId);
            return true;
        }

        long now = System.currentTimeMillis();
        Queue<Long> times = userOrderTimestamps.computeIfAbsent(userId, k -> new LinkedList<>());
        
        times.add(now);
        while (!times.isEmpty() && (now - times.peek() > 60000)) {
            times.poll();
        }

        if (times.size() >= 3) {
            System.out.println("🚨 FRAUD ALERT: " + userId + " placed 3 orders in under a minute!");
            return true;
        }
        return false;
    }
}