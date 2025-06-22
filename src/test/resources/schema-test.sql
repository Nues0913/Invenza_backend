-- 測試資料庫架構 (schema-test.sql)

-- 會員表
CREATE TABLE IF NOT EXISTS member (
    id VARCHAR(50) PRIMARY KEY,
    account VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(10) NOT NULL
);

-- 商品表
CREATE TABLE IF NOT EXISTS commodity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    stock_quantity DECIMAL(10,2),
    expected_import_quantity DECIMAL(10,2),
    expected_export_quantity DECIMAL(10,2)
);

-- 採購表
CREATE TABLE IF NOT EXISTS procurement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    commodity_name VARCHAR(100) NOT NULL,
    commodity_type VARCHAR(50),
    unit_price DECIMAL(10,2),
    quantity INT,
    supplier_name VARCHAR(100),
    supplier_id VARCHAR(50),
    supplier_email VARCHAR(100),
    supplier_phone VARCHAR(20),
    order_date TIMESTAMP,
    deadline_date TIMESTAMP,
    employee_name VARCHAR(100),
    employee_id VARCHAR(50),
    employee_email VARCHAR(100),
    employee_phone VARCHAR(20)
);

-- 訂單表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    commodity_name VARCHAR(100) NOT NULL,
    commodity_type VARCHAR(50),
    unit_price DECIMAL(10,2),
    quantity INT,
    dealer_name VARCHAR(100),
    dealer_id VARCHAR(50),
    dealer_email VARCHAR(100),
    dealer_phone VARCHAR(20),
    order_date TIMESTAMP,
    deadline_date TIMESTAMP,
    employee_name VARCHAR(100),
    employee_id VARCHAR(50),
    employee_email VARCHAR(100),
    employee_phone VARCHAR(20)
);
