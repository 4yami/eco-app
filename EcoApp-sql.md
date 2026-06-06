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
    purchased SMALLINT DEFAULT 0 NOT NULL,
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
VALUES ('alice', 'alice@campus.edu', 'password123', '555-0199', 2);

-- 3. Regular User acting as a Buyer (Bob)
INSERT INTO APP_USER (username, email, password, phone, role_id) 
VALUES ('bob', 'bob@campus.edu', 'securepass789', '555-0144', 2);

-- Seed Categories (IDs assigned dynamically: Electronics = 1, Books = 2, Furniture = 3)
INSERT INTO CATEGORY (name) VALUES ('Electronics');
INSERT INTO CATEGORY (name) VALUES ('Books & Education');
INSERT INTO CATEGORY (name) VALUES ('Furniture');

-- Seed Marketplace Items (Posted by Alice, seller_id = 2, and Bob, seller_id = 3)
INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('TI-84 Plus Calculator', 'Slightly used graphing calculator. Perfect for calculus.', 45.00, 'AVAILABLE', 'https://placehold.net/600x400.png', 2, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Dorm Desk Chair', 'Black ergonomic mesh chair, adjustable height.', 30.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('24-inch Gaming Monitor', '1080p 144Hz monitor. Excellent condition.', 110.00, 'AVAILABLE', 'https://placehold.co/1600x1400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('MacBook Pro 2020', '13-inch, 256GB SSD, 8GB RAM. Lightly used for school.', 650.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Introduction to Algorithms', 'CLRS textbook, 3rd edition. Like new condition.', 40.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Wooden Bookshelf', '3-tier tall bookshelf, solid pine wood, white finish.', 55.00, 'AVAILABLE', 'https://placehold.co/400', 3, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('iPhone 13 Pro Max', '128GB, Sierra Blue, with original box and charger.', 700.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Calculus: Early Transcendentals', 'Stewart 8th edition, some highlighting inside.', 25.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Twin XL Mattress Topper', 'Memory foam 3-inch mattress topper, hypoallergenic.', 35.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Sony WH-1000XM4 Headphones', 'Wireless noise-cancelling headphones, great battery life.', 180.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Organic Chemistry Textbook', 'Clayden 2nd edition, minor wear on cover.', 50.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Desk Lamp LED', 'Adjustable neck, 3 brightness levels, USB charging port.', 20.00, 'AVAILABLE', 'https://placehold.co/400', 3, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Samsung Galaxy Tab S7', '11-inch, 128GB, with S Pen included.', 350.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Linear Algebra Done Right', 'Axler 3rd edition, perfect condition.', 30.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Mini Fridge 3.2 cu ft', 'Compact fridge for dorm, black, energy-efficient.', 80.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Raspberry Pi 5 8GB Kit', 'Complete kit with case, power supply, and 64GB SD card.', 95.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('The Pragmatic Programmer', '20th anniversary edition, like new.', 22.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Dorm Room Rug', '5x7 ft soft shag rug, dark grey, barely used.', 28.00, 'AVAILABLE', 'https://placehold.co/400', 3, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Logitech MX Master 3S Mouse', 'Wireless ergonomic mouse, silent clicks.', 65.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Physics for Scientists and Engineers', 'Serway 9th edition, online access code included.', 55.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Dell XPS 13 Laptop', 'Intel i7, 16GB RAM, 512GB SSD, FHD display, great for coding.', 820.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Data Structures and Algorithms in Java', 'Robert Lafore, 2nd edition, clean pages.', 35.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Standing Desk Converter', 'Adjustable height, holds dual monitors, 36-inch wide.', 120.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Apple AirPods Pro 2', 'USB-C version, with noise cancellation, excellent condition.', 160.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Clean Code by Robert C. Martin', 'Signed copy, barely opened, must-have for devs.', 28.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Portable Bluetooth Speaker', 'JBL Flip 6, waterproof, deep bass, 12hr battery.', 70.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Dorm Loft Bed Frame', 'Metal frame, fits twin XL mattress, includes ladder.', 150.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Designing Data-Intensive Applications', 'Kleppmann, 1st edition, like new, highlighted a few pages.', 38.00, 'AVAILABLE', 'https://placehold.co/600x400', 2, 3);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('Electric Kettle', 'Stainless steel 1.7L, auto shut-off, boils water fast.', 18.00, 'AVAILABLE', 'https://placehold.co/400', 3, 2);

INSERT INTO ITEM (title, description, price, status, image_url, category_id, seller_id)
VALUES ('iPad Air M2', '11-inch, 128GB, Space Gray, with Apple Pencil Pro.', 550.00, 'AVAILABLE', 'https://placehold.co/600x400', 1, 3);

-- Seed Cart Items (Bob and others adding items to their shopping carts)
-- user_id = 3 (Bob), item_id = 1 (TI-84 Calculator)
INSERT INTO CART_ITEM (user_id, item_id, purchased) VALUES (3, 1, 0);
INSERT INTO CART_ITEM (user_id, item_id, purchased) VALUES (3, 7, 0);
INSERT INTO CART_ITEM (user_id, item_id, purchased) VALUES (2, 5, 0);
INSERT INTO CART_ITEM (user_id, item_id, purchased) VALUES (2, 12, 0);
INSERT INTO CART_ITEM (user_id, item_id, purchased) VALUES (3, 20, 0);