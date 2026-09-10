-- ============================================================
--  Circular Campus Marketplace - Database Schema
--  Portable DDL: runs on H2 (MODE=MySQL) and on MySQL 8.
-- ============================================================

CREATE TABLE IF NOT EXISTS students (
    student_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(100)  NOT NULL,
    email                 VARCHAR(150)  NOT NULL UNIQUE,
    password              VARCHAR(128)  NOT NULL,     -- SHA-256 hex hash
    wallet_balance        DECIMAL(10,2) NOT NULL DEFAULT 0,
    sustainability_points INT           NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS listings (
    listing_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id      BIGINT        NOT NULL,
    title          VARCHAR(150)  NOT NULL,
    description    VARCHAR(1000),
    category       VARCHAR(50)   NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    item_condition VARCHAR(30)   NOT NULL,            -- 'condition' is a reserved word
    status         VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE | SOLD
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_listing_seller FOREIGN KEY (seller_id) REFERENCES students(student_id)
);

-- Session-based cart, persisted per HttpSession id (as per the brief's schema).
CREATE TABLE IF NOT EXISTS cart_items (
    cart_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL,
    listing_id BIGINT       NOT NULL,
    added_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    txn_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id   BIGINT        NOT NULL,
    listing_id BIGINT        NOT NULL,
    amount     DECIMAL(10,2) NOT NULL,
    txn_date   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_buyer   FOREIGN KEY (buyer_id)   REFERENCES students(student_id),
    CONSTRAINT fk_txn_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);
