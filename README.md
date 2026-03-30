# Rupanagudi_Siva_Redd_Ecommerce_Order_Engine_Hackathon
📝 # Project Overview
This project is a high-performance, menu-driven CLI backend simulation for an e-commerce platform. It handles complex distributed system challenges like high concurrency, inventory consistency, and transaction reliability using JDK 25.

✨ Features Implemented
Inventory Management: Thread-safe product tracking with logical locking.

Multi-User Cart: Independent user carts with real-time stock reservation and automatic expiry (Task 15).

Order State Machine: Tracks orders from CREATED to SHIPPED or FAILED.

Transaction Rollback: Automatic stock restoration if payment fails (Task 7).

Fraud Detection: Flags high-value orders and rapid-fire order attempts (Task 17).

Chaos Engineering: Simulated failure injection system to test system recovery (Task 18).

Advanced Discounts: Tiered pricing and coupon code support (SAVE10, FLAT200).

🏗️ Design Approach
Microservice Simulation: The project is modularized into dedicated Services, Engines, and Models for loose coupling.
Concurrency Control: Utilized ConcurrentHashMap and AtomicInteger to prevent overselling without bottlenecking performance.
Atomic Operations: Order placement is handled as a single transaction to ensure data integrity.

💡 Assumptions
Inventory is stored in-memory for the duration of the application runtime.
Payment success is simulated with an 80% success rate to demonstrate rollback logic.
A "User ID" is used to distinguish between different cart sessions.

🛠️ How to Run the Project
Ensure you have JDK 25 installed.
Navigate to src/main/java.
Compile the project: javac com/ecommerce/Main.java.
Run the application: java com.ecommerce.Main.
