// File: src/main/java/com/ecommerce/services/CartService.java
package com.ecommerce.services;

import com.ecommerce.models.Product;
import com.ecommerce.engine.SystemUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CartService {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> userCarts = new ConcurrentHashMap<>();
    private final ProductService productService;
    
    // Task 15: Background timer for expiry
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    private ConcurrentHashMap<String, Integer> getOrCreateCart(String userId) {
        return userCarts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }

    public void addToCart(String userId, String productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            System.out.println("❌ Error: Product not found.");
            return;
        }

        if (product.reserveStock(quantity)) {
            ConcurrentHashMap<String, Integer> cart = getOrCreateCart(userId);
            cart.merge(productId, quantity, Integer::sum);
            System.out.println("✅ Added " + quantity + " of " + product.getName() + " to " + userId + "'s cart.");
            
            // Task 15: Automatically release reserved stock after 2 minutes
            scheduler.schedule(() -> {
                ConcurrentHashMap<String, Integer> currentCart = userCarts.get(userId);
                if (currentCart != null && currentCart.containsKey(productId)) {
                    SystemUtils.log("RESERVATION_EXPIRED: Releasing " + quantity + " units of " + productId + " for " + userId);
                    removeFromCart(userId, productId, quantity);
                }
            }, 2, TimeUnit.MINUTES);
            
        } else {
            System.out.println("❌ Error: Not enough available stock for " + product.getName() + ".");
        }
    }

    public void removeFromCart(String userId, String productId, int quantityToRemove) {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || !cart.containsKey(productId)) {
            System.out.println("❌ Error: Item not in cart.");
            return;
        }

        int currentQty = cart.get(productId);
        if (quantityToRemove >= currentQty) {
            cart.remove(productId);
            quantityToRemove = currentQty;
        } else {
            cart.put(productId, currentQty - quantityToRemove);
        }

        Product product = productService.getProduct(productId);
        if (product != null) product.releaseReservedStock(quantityToRemove);
        System.out.println("✅ Removed " + quantityToRemove + " units of " + productId + " from cart.");
    }

    public void viewCart(String userId) {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || cart.isEmpty()) {
            System.out.println("🛒 Cart for " + userId + " is empty.");
            return;
        }
        
        System.out.println("\n🛒 --- Cart for " + userId + " ---");
        cart.forEach((productId, qty) -> {
            Product p = productService.getProduct(productId);
            System.out.println("- " + p.getName() + " | Qty: " + qty + " | Subtotal: ₹" + (p.getPrice() * qty));
        });
    }
    
    public Map<String, Integer> getCartContents(String userId) { return userCarts.get(userId); }
    public void clearCart(String userId) { userCarts.remove(userId); }
}