package com.ecommerce.services;

import com.ecommerce.models.Product;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ProductService {
    private final ConcurrentHashMap<String, Product> inventory = new ConcurrentHashMap<>();

    public boolean addProduct(String id, String name, double price, int stock) {
        if (stock < 0) {
            System.out.println("❌ Error: Stock cannot be negative.");
            return false;
        }
        
        Product newProduct = new Product(id, name, price, stock);
        if (inventory.putIfAbsent(id, newProduct) != null) {
            System.out.println("❌ Error: Product ID '" + id + "' already exists.");
            return false;
        }
        
        System.out.println("✅ Product '" + name + "' added successfully.");
        return true;
    }

    public Product getProduct(String id) { return inventory.get(id); }
    public Collection<Product> getAllProducts() { return inventory.values(); }
}