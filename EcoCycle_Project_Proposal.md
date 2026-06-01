# Eco-App - Project Execution Plan 🚀

## Core Page Flows & Scope

### 🔐 Authentication Layer
* **Registration Page:** Collects `username`, `email`, `password`, and a mandatory `phone_number`. 
* **Login Page:** Authenticates credentials and extracts user permissions based on their assigned role type.
* *Rubric Fit:* Meets the homepage requirements for a structured login/registration system.

### 🛠️ Admin Module
* **Admin Dashboard:** Provides absolute user management rights, allowing administrators to view all registered users, modify accounts, or clear inactive profiles.
* *Rubric Fit:* Provides clear evidence of a system operating with **more than 1 user type** and serves as our primary **User CRUD** implementation.

### 🛒 Marketplace Dashboard (User Module)
* **Home Feed:** Allows authenticated users to look through listed items, drilling down by individual categories.
* **Item Management:** Active sellers can launch new product posts (Title, Description, Price, Category), modify product details, or clear out listings from the live market view.
* *Rubric Fit:* Establishes our primary **Item CRUD** flow covering item creation, retrieval, updates, and final removal.

### 🔄 Shopping Cart & Connection Workflow
* **Browsing & Cart Interaction:** Users can add items to their personal lists. This flags the item's status attribute as `IN_CART` to temporarily claim visibility.
* **Transaction Finalization:** Item detailed lookups show a direct link to initiate contact with the seller via Call/WhatsApp. Once a verbal agreement is reached, the seller switches the item status to `CLAIMED` via their inventory control dashboard.
* *Rubric Fit:* The transaction logic maps perfectly across our system state, and the cart linkage addresses the required **many-to-many relationship structural design pattern**.

---

## JavaDB (Apache Derby) SQL DDL Schema

Copy and execute the following queries inside the NetBeans Services window under your registered JavaDB connection:

```sql
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

-- Table 4: Marketplace Items (Updated with image_url)
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

-- Initial Base Data Seed Logic
INSERT INTO ROLE (role_name) VALUES ('ADMIN');
INSERT INTO ROLE (role_name) VALUES ('USER');

INSERT INTO CATEGORY (name) VALUES ('Electronics');
INSERT INTO CATEGORY (name) VALUES ('Books & Education');
INSERT INTO CATEGORY (name) VALUES ('Campus Services');
```

👥 4-Person Workload Distribution Matrix

To ensure independent development modules and avoid Git merge conflicts, the project tasks are distributed cleanly by vertical feature lines:
👤 Person 1: Database Setup & Authentication Layer (The Anchor)

    Frontend: Sign-In UI layout, Registration UI layout, and conditional navbar links tailored to authorization roles.

    Backend Development: * Initialize the shared NetBeans database connection manager (DBConnection.java).

        Build UserDAO and RoleDAO handling credential processing and profile writing.

        Create LoginServlet and RegisterServlet to manage active HTTP sessions (session.setAttribute("user", userObj)).

    Rubric Focus: System Authentication, password handling, and baseline session architecture.

👤 Person 2: Marketplace Feed & Discovery UI (The Reader)

    Frontend: Home feed core grid, dynamic category filtering sidebar, and the Item Detailed View display page/modal featuring WhatsApp click-to-action bindings.

    Backend Development: * Build CategoryDAO to extract filtering tags.

        Implement standard read-only queries in ItemDAO (getAllItems(), getItemsByCategory()).

        Add string parameter lookup controls using SQL LIKE %query% definitions inside MarketplaceServlet.

    Rubric Focus: Item Retrieval (R in CRUD) and multi-category indexing layouts.

👤 Person 3: Item Creation & Management Module (The Writer)

    Frontend: "Sell an Item" interface form (handling product metadata inputs along with the optional image_url field string) and the "My Listings" seller inventory dashboard.

    Backend Development: * Code data mutation mechanics inside ItemDAO (insertItem(), updateItem(), deleteItem()).

        Set up fallback checks within the Servlet layer to auto-assign a fallback placeholder image link if the optional image_url is left empty by the seller.

        Construct ItemManagementServlet handling secure data capture, entry sanitization, and automated routing back to inventory views.

    Rubric Focus: Item Mutation Lifecycles (C, U, D in CRUD).

👤 Person 4: Shopping Cart & Administrative Access Controls (The Link)

    Frontend: Shopping Cart side-drawer / active checkout overview page, alongside the Admin Control Console display showing a complete list of users with dynamic modifier actions.

    Backend Development: * Develop CartDAO to manipulate bridge table relationships (addToCart, removeFromCart, getCartByUserId).

        Establish administrative functions within UserDAO to read or terminate inactive user metrics (getAllUsers(), deleteUser()).

        Implement access restriction handlers (CartServlet, AdminManagementServlet).

    Rubric Focus: Many-to-Many Bridge Table integration and Multi-User Role validation rules.