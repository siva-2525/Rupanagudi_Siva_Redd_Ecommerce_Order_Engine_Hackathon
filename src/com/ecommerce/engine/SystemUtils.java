package com.ecommerce.engine;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class SystemUtils {
    public static void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] AUDIT: " + message);
    }

    private static final ConcurrentHashMap<String, Boolean> processedRequests = new ConcurrentHashMap<>();
    public static boolean isDuplicateRequest(String idempotencyKey) {
        return processedRequests.putIfAbsent(idempotencyKey, true) != null;
    }

    private static boolean failureModeActive = false;
    private static final Random rand = new Random();
    
    public static void toggleFailureMode() {
        failureModeActive = !failureModeActive;
        System.out.println("⚠️ Chaos Mode (Failure Injection): " + (failureModeActive ? "ON" : "OFF"));
    }

    public static boolean injectRandomFailure(String component) {
        if (failureModeActive && rand.nextInt(100) < 30) {
            System.out.println("⚡ INJECTED FAILURE in " + component);
            return true;
        }
        return false;
    }
}