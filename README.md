# Gold Rim Shop (Object-Oriented E-Commerce System)

## Overview
A desktop e-commerce and retail management platform developed in Java (Swing) connected to a MySQL database via JDBC. Engineered for the Object-Oriented System Design module (SEN5000) under the Cardiff Metropolitan University curriculum. Grade Achieved: 80% (First-Class Honours / Distinction).

## Core OOP & System Architecture
- **Inheritance Hierarchy:** Abstract `User` base class inherited by specialized `Shopper` and `Administrator` roles with distinct method behaviors.
- **Data Encapsulation:** Critical domain models (e.g., `Product.java`) strictly encapsulate attributes with accessor/mutator methods to maintain transactional integrity.
- **Algorithmic Order Tracking:** Logic-driven status engine (`OrderTracker.java`) calculating order progression ("Prepared" vs. "Shipped") automatically from timestamp differentials.
- **Relational Persistence (3NF):** Normalized MySQL schema mapping Users, Products, Orders, OrderDetails, and real-time SupportMessages with full foreign key constraints.
- **Role-Based Workflows:** Customer catalog navigation, session-based shopping cart management, payment validation, and administrative CRUD inventory controls.

## Technologies Used
- **Programming Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing
- **Database:** MySQL via JDBC Driver (`mysql-connector-j-9.6.0.jar`)
- **System Design:** UML Use Case, Class Diagram, and Entity-Relationship Diagram (ERD)

## Project Structure
- `Main.java` - Application entry point and database initializer
- `LoginForm.java` / `RegisterForm.java` - Authentication and role-based redirection
- `CatalogForm.java` / `CartForm.java` / `OrderForm.java` - Customer storefront, cart session, and checkout validation
- `OrderTracker.java` - Dynamic order status calculation engine
- `AdminDashboard.java` - Central administrative CRUD panel for stock and orders
- `ChatWindow.java` - Client-side polled real-time customer support module
- `st20320120 SEN5000 PORT1.pdf` - Complete architectural coursework report and diagrams

## How to Run
```bash
# Compile with MySQL Connector
javac -cp ".:mysql-connector-j-9.6.0.jar" *.java

# Execute Application
java -cp ".:mysql-connector-j-9.6.0.jar" Main
