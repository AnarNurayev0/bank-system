-- DROP TABLES IN CORRECT ORDER (DEPENDENCIES FIRST)
DROP TABLE IF EXISTS transaction_history CASCADE;
DROP TABLE IF EXISTS card CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS pay_provider CASCADE;
DROP TABLE IF EXISTS registration_otp CASCADE;
DROP TABLE IF EXISTS reset_pin_code CASCADE;

-- 1. CUSTOMER TABLE
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    email_password VARCHAR(255),
    telephone VARCHAR(255),
    birth_date DATE
);

-- 2. PAY PROVIDER TABLE
CREATE TABLE pay_provider (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    description VARCHAR(255),
    cashback_percent DECIMAL(5,2),
    active BOOLEAN DEFAULT TRUE
);

-- 3. REGISTRATION OTP TABLE
CREATE TABLE registration_otp (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE
);

-- 4. CARD TABLE
CREATE TABLE card (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customer(id) ON DELETE CASCADE,
    card_number VARCHAR(255),
    card_password VARCHAR(255),
    cvv VARCHAR(255),
    expiration_date DATE,
    balance DECIMAL(19,2) DEFAULT 0,
    credit_limit DECIMAL(19,2) DEFAULT 0,
    used_limit DECIMAL(19,2) DEFAULT 0,
    currency VARCHAR(10), -- AZN, USD, EUR
    card_brand VARCHAR(20), -- VISA, MASTERCARD, AMEX
    card_type VARCHAR(20), -- DEBIT, CREDIT, CASHBACK
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- 5. TRANSACTION HISTORY TABLE
CREATE TABLE transaction_history (
    id SERIAL PRIMARY KEY,
    owner_card_id INT, -- Removed FK constraint to allow keeping history if card is deleted, or could add FK
    from_card_number VARCHAR(255),
    to_card_number VARCHAR(255),
    from_customer_name VARCHAR(255),
    to_customer_name VARCHAR(255),
    amount DECIMAL(19,2),
    converted_amount DECIMAL(19,2),
    type VARCHAR(50), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. RESET PIN CODE TABLE
CREATE TABLE reset_pin_code (
    id SERIAL PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE
);

-- =============================================
-- MOCK DATA INSERTS
-- =============================================

-- Insert Customers
INSERT INTO customer (full_name, email, email_password, telephone, birth_date)
VALUES 
('Anar Nurayev', 'anar@example.com', '12345', '+994501234567', '2000-01-01'),
('Ali Valiyev', 'ali@example.com', '12345', '+994551234567', '1995-05-15'),
('Leyla Aliyeva', 'leyla@example.com', '12345', '+994701234567', '1998-08-20');

-- Insert Pay Providers
INSERT INTO pay_provider (name, description, cashback_percent, active)
VALUES 
('Azercell', 'Mobile Operator', 2.0, true),
('Bakcell', 'Mobile Operator', 2.0, true),
('Nar', 'Mobile Operator', 2.0, true),
('Azerishig', 'Electricity Utility', 1.0, true),
('Azersu', 'Water Utility', 1.0, true),
('Azeriqaz', 'Gas Utility', 1.0, true);

-- Insert Cards

-- Anar's Cards
INSERT INTO card (customer_id, card_number, card_password, cvv, expiration_date, balance, currency, card_brand, card_type, status)
VALUES
(1, '4111111111111111', '1234', '123', '2028-12-31', 100.00, 'AZN', 'VISA', 'DEBIT', 'ACTIVE'),
(1, '5111111111111111', '1234', '456', '2029-06-30', 500.00, 'USD', 'MASTERCARD', 'CREDIT', 'ACTIVE'),
(1, '370000000000001', '1234', '789', '2030-01-01', 20.00, 'AZN', 'AMEX', 'CASHBACK', 'ACTIVE');

-- Ali's Cards
INSERT INTO card (customer_id, card_number, card_password, cvv, expiration_date, balance, currency, card_brand, card_type, status)
VALUES
(2, '4222222222222222', '0000', '111', '2027-11-15', 250.50, 'AZN', 'VISA', 'DEBIT', 'ACTIVE');

-- Leyla's Cards
INSERT INTO card (customer_id, card_number, card_password, cvv, expiration_date, balance, currency, card_brand, card_type, status)
VALUES
(3, '5333333333333333', '1111', '222', '2026-05-20', 1000.00, 'EUR', 'MASTERCARD', 'DEBIT', 'ACTIVE');

-- Insert Transactions (Mock History)
INSERT INTO transaction_history (owner_card_id, from_card_number, to_card_number, from_customer_name, to_customer_name, amount, type, created_at)
VALUES
(1, 'CASH', '4111111111111111', 'ATM', 'Anar Nurayev', 50.00, 'DEPOSIT', NOW() - INTERVAL '1 day'),
(1, '4111111111111111', 'Azercell', 'Anar Nurayev', 'Azercell', 10.00, 'PAYMENT', NOW() - INTERVAL '2 hour'),
(2, '5111111111111111', '4222222222222222', 'Anar Nurayev', 'Ali Valiyev', 20.00, 'TRANSFER', NOW() - INTERVAL '30 minute');
