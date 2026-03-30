package com.ecommerce;

import com.ecommerce.services.*;
import com.ecommerce.engine.SystemUtils;
import com.ecommerce.models.enums.OrderState;

import java.util.Scanner;

public class Main {
    private static final ProductService productService = new ProductService();
    private static final CartService cartService = new CartService(productService);
    private static final PaymentService paymentService = new PaymentService();
    private static final OrderService orderService = new OrderService(cartService, productService, paymentService);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        // Seed some data to play with immediately
        productService.addProduct("P1", "Laptop", 50000.0, 10);
        productService.addProduct("P2", "Mouse", 1000.0, 50);

        System.out.println("🚀 Distributed E-Commerce Order Engine Live!");

        while (running) {
            printMenu();
            System.out.print("Select an option: ");
            if (!scanner.hasNextInt()) {
                scanner.next(); continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Name: "); String name = scanner.nextLine();
                        System.out.print("Price: "); double price = scanner.nextDouble();
                        System.out.print("Stock: "); int stock = scanner.nextInt();
                        productService.addProduct(id, name, price, stock);
                    }
                    case 2 -> productService.getAllProducts().forEach(p -> 
                        System.out.println(p.getProductId() + " - " + p.getName() + " (Stock: " + p.getAvailableStock() + ")"));
                    case 3 -> {
                        System.out.print("User ID: "); String uId = scanner.nextLine();
                        System.out.print("Product ID: "); String pId = scanner.nextLine();
                        System.out.print("Qty: "); int q = scanner.nextInt();
                        cartService.addToCart(uId, pId, q);
                    }
                    case 4 -> {
                        System.out.print("User ID: "); String uId = scanner.nextLine();
                        System.out.print("Product ID: "); String pId = scanner.nextLine();
                        System.out.print("Qty to remove: "); int q = scanner.nextInt();
                        cartService.removeFromCart(uId, pId, q);
                    }
                    case 5 -> {
                        System.out.print("User ID: "); String uId = scanner.nextLine();
                        cartService.viewCart(uId);
                    }
                    case 6 -> System.out.println("Coupons are applied automatically at checkout! (Try SAVE10 or FLAT200)");
                    case 7 -> {
                        System.out.print("User ID: "); String uId = scanner.nextLine();
                        System.out.print("Coupon Code (or press Enter to skip): "); String code = scanner.nextLine();
                        orderService.placeOrder(uId, code);
                    }
                    case 8 -> {
                        System.out.print("Order ID to Cancel: ");
                        orderService.cancelOrder(scanner.nextLine());
                    }
                    case 9 -> {
                        // Updated for Task 11: Search & Filter
                        System.out.println("1. View All Orders | 2. Search by ID | 3. Filter by Status");
                        System.out.print("Choose option: ");
                        int subChoice = scanner.nextInt(); scanner.nextLine();
                        if (subChoice == 1) {
                            orderService.viewOrders();
                        } else if (subChoice == 2) {
                            System.out.print("Enter Order ID (e.g., ORD-1234ABCD): ");
                            orderService.getOrderById(scanner.nextLine());
                        } else if (subChoice == 3) {
                            System.out.print("Enter Status (CREATED, PENDING_PAYMENT, PAID, SHIPPED, DELIVERED, FAILED, CANCELLED): ");
                            try {
                                OrderState state = OrderState.valueOf(scanner.nextLine().toUpperCase());
                                orderService.filterOrdersByState(state);
                            } catch (IllegalArgumentException e) {
                                System.out.println("❌ Invalid status entered.");
                            }
                        } else {
                            System.out.println("Invalid option.");
                        }
                    }
                    case 10 -> {
                        System.out.println("⚠️ Low Stock Alerts:");
                        productService.getAllProducts().stream()
                                .filter(p -> p.getAvailableStock() < 5)
                                .forEach(p -> System.out.println("- " + p.getName() + " has " + p.getAvailableStock() + " left!"));
                    }
                    case 11 -> {
                        System.out.print("Order ID: "); String oId = scanner.nextLine();
                        System.out.print("Product ID to return: "); String pId = scanner.nextLine();
                        System.out.print("Qty to return: "); int q = scanner.nextInt();
                        orderService.returnProduct(oId, pId, q);
                    }
                    case 12 -> {
                        System.out.println("🧵 Simulating 3 users fighting for the same product...");
                        Runnable task = () -> cartService.addToCart(Thread.currentThread().getName(), "P1", 1);
                        new Thread(task, "UserA").start();
                        new Thread(task, "UserB").start();
                        new Thread(task, "UserC").start();
                    }
                    case 13 -> System.out.println("Logs are printed to console in real-time. Scroll up to view.");
                    case 14 -> SystemUtils.toggleFailureMode();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("❌ Input error. Please try again.");
                scanner.nextLine(); // Clear bad input
            }
        }
        System.out.println("Engine offline.");
        // Task 15 Cleanup: Shut down the scheduler when exiting
        System.exit(0); 
    }
    
    private static void printMenu() {
        System.out.println("\n--- 💻 CLI Menu ---");
        System.out.println("1. Add Product | 2. View Products | 3. Add to Cart | 4. Remove from Cart");
        System.out.println("5. View Cart   | 6. Apply Coupon  | 7. Place Order   | 8. Cancel Order");
        System.out.println("9. View Orders | 10. Low Stock Alert | 11. Return Product | 12. Sim Concurrent");
        System.out.println("13. View Logs  | 14. Trigger Failure Mode | 0. Exit");
    }
}