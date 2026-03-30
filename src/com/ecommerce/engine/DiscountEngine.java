package com.ecommerce.engine;

import java.util.Map;

public class DiscountEngine {
    public double applyDiscounts(double total, Map<String, Integer> items, String coupon) {
        double discount = 0;
        if (total > 1000) discount += total * 0.10;
        
        for (int qty : items.values()) {
            if (qty > 3) {
                discount += total * 0.05;
                break;
            }
        }
        
        if ("SAVE10".equalsIgnoreCase(coupon)) discount += total * 0.10;
        else if ("FLAT200".equalsIgnoreCase(coupon)) discount += 200;

        return Math.max(0, total - discount);
    }
}