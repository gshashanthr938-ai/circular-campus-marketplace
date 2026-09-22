-- ============================================================
--  Circular Campus Marketplace - Database Schema
--  Portable DDL: runs on H2 (MODE=MySQL) and on MySQL 8.
-- ============================================================

CREATE TABLE IF NOT EXISTS students (
    student_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(100)  NOT NULL,
    email                 VARCHAR(150)  NOT NULL UNIQUE,
    phone                 VARCHAR(20),
    password              VARCHAR(128)  NOT NULL,     -- salted PBKDF2 encoding
    role                  VARCHAR(20)   NOT NULL DEFAULT 'STUDENT',
    wallet_balance        DECIMAL(10,2) NOT NULL DEFAULT 0,
    sustainability_points INT           NOT NULL DEFAULT 0,
    created_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS listings (
    listing_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id      BIGINT        NOT NULL,
    title          VARCHAR(150)  NOT NULL,
    description    VARCHAR(1000),
    category       VARCHAR(50)   NOT NULL,
    price          DECIMAL(10,2) NOT NULL,
    item_condition VARCHAR(30)   NOT NULL,            -- 'condition' is a reserved word
    status         VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE | SOLD | REMOVED
    image_path     VARCHAR(255),
    moderation_note VARCHAR(500),
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
    payment_method VARCHAR(20) NOT NULL DEFAULT 'UPI',
    payment_reference VARCHAR(80),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    terms_accepted_at TIMESTAMP,
    handover_code VARCHAR(6) NOT NULL,
    fulfillment_status VARCHAR(25) NOT NULL DEFAULT 'AWAITING_PICKUP',
    pickup_completed_at TIMESTAMP,
    txn_date   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_buyer   FOREIGN KEY (buyer_id)   REFERENCES students(student_id),
    CONSTRAINT fk_txn_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);

CREATE TABLE IF NOT EXISTS listing_images (
    image_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id  BIGINT NOT NULL,
    image_path  VARCHAR(255) NOT NULL,
    position_no INT NOT NULL,
    CONSTRAINT uq_listing_image_position UNIQUE (listing_id, position_no),
    CONSTRAINT fk_image_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);

CREATE TABLE IF NOT EXISTS waitlist (
    waitlist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id  BIGINT NOT NULL,
    student_id  BIGINT NOT NULL,
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_waitlist UNIQUE (listing_id, student_id),
    CONSTRAINT fk_waitlist_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id),
    CONSTRAINT fk_waitlist_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id       BIGINT NOT NULL,
    message          VARCHAR(500) NOT NULL,
    link_path        VARCHAR(255),
    is_read          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    txn_id      BIGINT NOT NULL UNIQUE,
    listing_id  BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    rating      INT NOT NULL,
    comment     VARCHAR(800),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_txn FOREIGN KEY (txn_id) REFERENCES transactions(txn_id),
    CONSTRAINT fk_review_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id),
    CONSTRAINT fk_review_student FOREIGN KEY (reviewer_id) REFERENCES students(student_id)
);
