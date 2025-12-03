-- butun cedvelleri silirik (silmemisden evvel error verirdi run edende)
DROP TABLE IF EXISTS Transactions CASCADE;
DROP TABLE IF EXISTS Card CASCADE;
DROP TABLE IF EXISTS Account CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;

CREATE TABLE Customer (
    CustomerID SERIAL PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(20),
    Address TEXT,
    BirthDate DATE
);

CREATE INDEX idx_customer_email ON Customer(Email);
CREATE INDEX idx_customer_phone ON Customer(PhoneNumber);

CREATE TABLE Account (
    AccountID SERIAL PRIMARY KEY,
    CustomerID INT REFERENCES Customer(CustomerID) ON DELETE CASCADE,
    AccountNumber VARCHAR(20) UNIQUE NOT NULL,
    Balance DECIMAL(15,2) DEFAULT 0,
    AccountType VARCHAR(20) CHECK (AccountType IN ('debet','kredit','deposit')),
    Currency VARCHAR(10),
    Status VARCHAR(10) CHECK (Status IN ('active','blocked')) DEFAULT 'active',
    ExpireDate DATE
);

CREATE INDEX idx_account_number ON Account(AccountNumber);

CREATE TABLE Transactions (
    TransactionID SERIAL PRIMARY KEY,
    TransactionType VARCHAR(20) CHECK (TransactionType IN ('transfer','online payment','cash withdrawal','deposit')),
    Amount DECIMAL(15,2) NOT NULL,
    SenderAccountID INT REFERENCES Account(AccountID),
    ReceiverAccountID INT REFERENCES Account(AccountID),
    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Note TEXT,
    Status VARCHAR(20) CHECK (Status IN ('pending','completed','failed')) DEFAULT 'pending'
);

CREATE TABLE Card (
    CardID SERIAL PRIMARY KEY,
    AccountID INT REFERENCES Account(AccountID) ON DELETE CASCADE,
    CardNumber VARCHAR(20) UNIQUE NOT NULL,
    CVV VARCHAR(5) NOT NULL,
    ExpireDate VARCHAR(10),
    CardType VARCHAR(20) CHECK (CardType IN ('visa','mastercard','american express'))
);

CREATE INDEX idx_card_number ON Card(CardNumber);

-- musteriler
INSERT INTO Customer (FullName, Password, Email, PhoneNumber, Address, BirthDate)
VALUES 
('Əli Vəli', '$2a$12$examplehash', 'ali@example.com', '+994501234567', 'Baku, Azerbaijan', '1995-03-15'),
('Aysel Məmmədova', '$2a$12$examplehash2', 'aysel@example.com', '+994502345678', 'Baku, Azerbaijan', '1998-07-22'),
('Banu Zamanova', '$2a$12$examplehash3', 'banu@example.com', '+994503456789', 'Shaki, Azerbaijan', '1993-09-27'),
('Rza Həsənov', '$2a$12$examplehash4', 'rza@example.com', '+994504567890', 'Ganja, Azerbaijan', '1989-03-21'),
('Elnarə Əlili', '$2a$12$examplehash5', 'elnare@example.com', '+994505678909', 'Baku, Azerbaijan', '1990-05-13');

-- hesablar
INSERT INTO Account (CustomerID, AccountNumber, Balance, AccountType, Currency, Status, ExpireDate)
VALUES
(1, '123456789012', 1500.00, 'debet', 'AZN', 'active', '2030-12-31'),
(1, '987654321098', 5000.00, 'deposit', 'USD', 'active', '2032-06-30'),
(2, '112233445566', 250.00, 'kredit', 'AZN', 'active', '2029-11-30');

-- kartlar
INSERT INTO Card (AccountID, CardNumber, CVV, ExpireDate, CardType)
VALUES
(1, '4111111111111111', '123', '12/26', 'visa'),
(1, '5500000000000004', '456', '11/25', 'mastercard'),
(3, '340000000000009',  '789', '01/27', 'american express');

-- transactionlar
INSERT INTO Transactions (TransactionType, Amount, SenderAccountID, ReceiverAccountID, Note, Status)
VALUES
('deposit',         1000.00, NULL, 1, 'Initial deposit', 'completed'),
('transfer',         200.00, 1,    3, 'Payment for services', 'completed'),
('cash withdrawal',   50.00, 2,   NULL, 'ATM withdrawal', 'completed'),
('online payment',    30.00, 1,    2, 'Online shopping', 'pending');

--bu hisseye "select * from Card, Account ve s" yaziriq

