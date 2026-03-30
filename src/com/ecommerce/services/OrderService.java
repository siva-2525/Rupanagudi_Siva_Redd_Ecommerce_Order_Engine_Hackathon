package com.ecommerce.services;

import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.models.enums.OrderState;
import com.ecommerce.engine.DiscountEngine;
import com.ecommerce.engine.FraudDetectionSystem;
import com.ecommerce.engine.SystemUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {
    private final CartService cartService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final DiscountEngine discountEngine;
    private final FraudDetectionSystem fraudSystem;
    private final ConcurrentHashMap<String, Order> orderDatabase = new ConcurrentHashMap<>();

    public OrderService(CartService cartService, ProductService productService, PaymentService paymentService) {
        this.cartService = cartService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.discountEngine = new DiscountEngine();
        this.fraudSystem = new FraudDetectionSystem();
    }

    public Order placeOrder(String userId, String couponCode) {
        if (SystemUtils.isDuplicateRequest(userId + "-checkout-" + System.currentTimeMillis() / 10000)) {
            System.out.println("⏳ Request already processing. Please wait.");
            return null;
        }

        Map<String, Integer> cart = cartService.getCartContents(userId);
        if (cart == null || cart.isEmpty()) return null;

        double total = cart.entrySet().stream()
                .mapToDouble(e -> productService.getProduct(e.getKey()).getPrice() * e.getValue())
                .sum();

        total = discountEngine.applyDiscounts(total, cart, couponCode);

        if (fraudSystem.isFraudulent(userId, total)) return null;

        if (SystemUtils.injectRandomFailure("OrderCreation")) {
            SystemUtils.log("ORDER_CREATION_FAILED for " + userId);
            return null;
        }

        Order order = new Order(userId, Map.copyOf(cart), total);
        orderDatabase.put(order.getOrderId(), order);
        SystemUtils.log("ORDER_CREATED: " + order.getOrderId());
        cartService.clearCart(userId);

        if (paymentService.processPayment(order.getOrderId(), total)) {
            order.setState(OrderState.PAID);
            cart.forEach((id, qty) -> productService.getProduct(id).confirmSale(qty));
            order.setState(OrderState.SHIPPED);
            SystemUtils.log("PAYMENT_SUCCESS for " + order.getOrderId());
        } else {
            order.setState(OrderState.FAILED);
            cart.forEach((id, qty) -> productService.getProduct(id).releaseReservedStock(qty));
            SystemUtils.log("PAYMENT_FAILED - Rolled back stock for " + order.getOrderId());
        }
        return order;
    }

    public void cancelOrder(String orderId) {
        Order order = orderDatabase.get(orderId);
        if (order == null) {
            System.out.println("❌ Order not found.");
            return;
        }
        if (order.getState() == OrderState.CANCELLED || order.getState() == OrderState.DELIVERED) {
            System.out.println("❌ Cannot cancel order in state: " + order.getState());
            return;
        }

        order.setState(OrderState.CANCELLED);
        order.getItems().forEach((id, qty) -> productService.getProduct(id).releaseReservedStock(qty));
        SystemUtils.log("ORDER_CANCELLED: " + orderId + " (Stock restored)");
        System.out.println("✅ Order " + orderId + " cancelled successfully.");
    }

    public void returnProduct(String orderId, String productId, int qty) {
        Order order = orderDatabase.get(orderId);
        if (order == null || order.getState() != OrderState.SHIPPED) {
            System.out.println("❌ Invalid order or order not shipped yet.");
            return;
        }
        
        Integer currentQty = order.getItems().get(productId);
        if (currentQty == null || qty > currentQty) {
             System.out.println("❌ Invalid return quantity.");
             return;
        }
        
        Product p = productService.getProduct(productId);
        p.releaseReservedStock(qty);
        SystemUtils.log("RETURN_PROCESSED: " + qty + " units of " + productId + " from " + orderId);
        System.out.println("✅ Return processed and stock updated.");
    }

    public void viewOrders() { orderDatabase.values().forEach(System.out::println); }
 // Task 11: Search by order ID
    public void getOrderById(String orderId) {
        Order order = orderDatabase.get(orderId);
        if (order != null) {
            System.out.println(order);
        } else {
            System.out.println("❌ Order not found.");
        }
    }

    // Task 11: Filter by state
    public void filterOrdersByState(OrderState state) {
        boolean found = false;
        for (Order order : orderDatabase.values()) {
            if (order.getState() == state) {
                System.out.println(order);
                found = true;
            }
        }
        if (!found) {
            System.out.println("📭 No orders found with status: " + state);
        }
    }
}