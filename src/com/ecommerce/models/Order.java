package com.ecommerce.models;

import com.ecommerce.models.enums.OrderState;
import java.util.Map;
import java.util.UUID;

public class Order {
    private final String orderId;
    private final String userId;
    private final Map<String, Integer> items;
    private final double totalAmount;
    private OrderState state;

    public Order(String userId, Map<String, Integer> items, double totalAmount) {
        this.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.state = OrderState.CREATED;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public Map<String, Integer> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    
    @Override
    public String toString() {
        return String.format("Order ID: %s | User: %s | Total: ₹%.2f | Status: %s", 
                orderId, userId, totalAmount, state);
    }
}