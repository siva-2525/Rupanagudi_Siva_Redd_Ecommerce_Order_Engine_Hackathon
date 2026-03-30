package com.ecommerce.models;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Product {
    private final String productId;
    private final String name;
    private final double price;
    private final AtomicInteger availableStock;
    private final AtomicInteger reservedStock; // For Task 3: Real-Time Reservation
    public final ReentrantLock lock; // For Task 4: Logical locking

    public Product(String productId, String name, double price, int initialStock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.availableStock = new AtomicInteger(initialStock);
        this.reservedStock = new AtomicInteger(0);
        this.lock = new ReentrantLock();
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    
    public int getAvailableStock() { return availableStock.get(); }
    
    // Thread-safe reservation
    public boolean reserveStock(int quantity) {
        while (true) {
            int currentAvailable = availableStock.get();
            if (currentAvailable < quantity) {
                return false; // Not enough stock
            }
            if (availableStock.compareAndSet(currentAvailable, currentAvailable - quantity)) {
                reservedStock.addAndGet(quantity);
                return true;
            }
        }
    }

    public void releaseReservedStock(int quantity) {
        reservedStock.addAndGet(-quantity);
        availableStock.addAndGet(quantity);
    }
    
    public void confirmSale(int quantity) {
        reservedStock.addAndGet(-quantity);
        // Stock was already removed from availableStock during reservation
    }
}