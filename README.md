# 🏦 NexusBank - Advanced Core Banking System

NexusBank is a state-of-the-art **Core Banking Backend API** designed to provide a seamless financial experience. Built with **Spring Boot 3.x** and **Java 21**, it manages complex banking operations ranging from multi-type card logic to automated cashback systems and secure transaction processing.

---

## 🚀 Vision & Purpose
The goal of NexusBank is to provide a developer-friendly, secure, and high-performance banking foundation that can handle real-world financial scenarios like dynamic credit limits, multi-step authentication (OTP), and complex utility payment structures.

---

## 🛠️ Core Features & Business Logic

### 1. **User Lifecycle & Security**
- **Smart Registration**: Users undergo a multi-step registration process backed by **Email OTP (One-Time Password)** verification.
- **Secure Authentication**: Password management and PIN reset flows are protected with verification codes.
- **Role-Based Access**: Specialized endpoints for everyday users and **Super Admin** management.

### 2. **Professional Card Management System**
NexusBank doesn't just store card numbers; it implements distinct financial logic for different tiers:
- **Debit Cards**: Real-time balance validation for every transaction.
- **Credit Cards**: 
  - Dynamic **Credit Limit** management.
  - Tracking of **Used Limit** vs **Total Limit**.
  - Intelligent balance calculation (Own Funds + Available Credit).
- **Cashback Cards**: Integrated loyalty logic that rewards users automatically after specific utility payments.
- **Card Security**: Automated expiration checks via **Spring Schedulers**.

### 3. **High-Performance Transaction Engine**
- **P2P Transfers**: Secure wallet-to-wallet transfers with instant balance updates across ACID-compliant database operations.
- **Utility Payments**: A modular system allowing payments for:
  - Mobile Operators (Azercell, Bakcell, etc.)
  - Utility Services (Electricity, Gas, Water)
  - Internet and TV.
- **Automated Cashback**: Logic-driven reward system where a percentage of utility payments is instantly refunded to the user's cashback balance.
- **ATM Services**: Simulated cash-in (Deposit) and cash-out (Withdrawal) functionalities.

### 4. **Administrative Control Center**
- **Global Settings**: Admins can modify cashback rates across the platform in real-time.
- **Provider Management**: Add, update, or deactivate payment providers (e.g., adding a new utility company).

---

## 🏗️ Technical Architecture

### **Tech Stack**
- **Backend**: Java 21 & Spring Boot 3.3.x
- **Database**: PostgreSQL (Relational Data Modeling)
- **Data Access**: Spring Data JPA & Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Email Service**: Spring Boot Starter Mail (SMTP Integration)
- **Project Management**: Maven

### **Project Structure Highlights**
- `controller/`: REST API Endpoints.
- `service/`: Core business logic (Debit/Credit calculations, Transaction rules).
- `repository/`: Data persistence layer.
- `scheduler/`: Background tasks for card status and expiration updates.
- `dto/`: Clean data transfer objects for API security and efficiency.

---

## 📖 API Documentation (Swagger)

The entire API is fully interactive. You can explore inputs, outputs, and test the endpoints directly.

👉 **Access Swagger UI:** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

---

## ⚙️ How to Setup & Run

### 1. Database Configuration
1. Ensure **PostgreSQL** is running.
2. Create a database named `bank_db`.
3. Run the provided `script.sql` to initialize tables and seed data.

### 2. Application Setup
Update `src/main/resources/application.properties` with your environment details:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# SMTP Settings for OTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
