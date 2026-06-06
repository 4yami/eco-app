-- ============================================================================
-- 1. CLEANUP / DROP EXISTING TABLES (Order matters due to Foreign Keys!)
-- ============================================================================
DROP TABLE CART_ITEM;
DROP TABLE ITEM;
DROP TABLE APP_USER;
DROP TABLE CATEGORY;
DROP TABLE ROLE;

-- ============================================================================
-- 2. SCHEMA DEFINITION
-- ============================================================================

-- Table 1: System Roles
CREATE TABLE ROLE (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    role_name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- Table 2: User Profiles
CREATE TABLE APP_USER (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES ROLE(id) ON DELETE SET NULL
);

-- Table 3: Product Categories
CREATE TABLE CATEGORY (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- Table 4: Marketplace Items
CREATE TABLE ITEM (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    image_url VARCHAR(2048),
    category_id INT,
    seller_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES CATEGORY(id) ON DELETE SET NULL,
    FOREIGN KEY (seller_id) REFERENCES APP_USER(id) ON DELETE CASCADE
);

-- Table 5: Many-to-Many Bridge Table (Shopping Cart Allocation)
CREATE TABLE CART_ITEM (
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES APP_USER(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES ITEM(id) ON DELETE CASCADE
);

-- ============================================================================
-- 3. BASE DATA SEED LOGIC
-- ============================================================================

-- Seed Roles (IDs assigned dynamically: ADMIN = 1, USER = 2)
INSERT INTO ROLE (role_name) VALUES ('ADMIN');
INSERT INTO ROLE (role_name) VALUES ('USER');

-- Seed User Logins
-- 1. System Administrator
INSERT INTO APP_USER (username, email, password, phone, role_id) 
VALUES ('admin', 'admin@campusmarketplace.com', 'adminpass123', '555-0100', 1);

-- 2. Regular User acting as a Seller (Alice)
INSERT INTO APP_USER (username, email, password, phone, role_id) 
VALUES ('alice_seller', 'alice@campus.edu', 'password123', '555-0199', 2);

-- 3. Regular User acting as a Buyer (Bob)
INSERT INTO APP_USER (username, email, password, phone, role_id) 
VALUES ('bob_buyer', 'bob@campus.edu', 'securepass789', '555-0144', 2);

-- Seed Categories (IDs assigned dynamically: Electronics = 1, Books = 2, Furniture = 3)
INSERT INTO CATEGORY (name) VALUES ('Electronics');
INSERT INTO CATEGORY (name) VALUES ('Books & Education');
INSERT INTO CATEGORY (name) VALUES ('Furniture');

-- Seed Marketplace Items (Posted by Alice, seller_id = 2)
INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('TI-84 Plus Calculator', 'Slightly used graphing calculator. Perfect for calculus.', 45.00, 'AVAILABLE', 'https://example.com/images/ti84.jpg', 2, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Dorm Desk Chair', 'Black ergonomic mesh chair, adjustable height.', 30.00, 'AVAILABLE', 'https://example.com/images/chair.jpg', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('24-inch Gaming Monitor', '1080p 144Hz monitor. Excellent condition.', 110.00, 'AVAILABLE', 'https://example.com/images/monitor.jpg', 1, 2);

-- Seed Cart Item (Bob adds Alice's Calculator to his shopping cart)
-- user_id = 3 (Bob), item_id = 1 (TI-84 Calculator)
INSERT INTO CART_ITEM (user_id, item_id) VALUES (3, 1);

ALTER TABLE CART_ITEM ADD PURCHASED SMALLINT DEFAULT 0 NOT NULL;