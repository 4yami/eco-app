# Eco-App - Project Execution Plan 🚀

## Core Page Flows & Scope

### 🔐 Authentication Layer
* *Registration Page:* Collects username, email, password, and a mandatory phone_number. 
* *Login Page:* Authenticates credentials and extracts user permissions based on their assigned role type.
* Rubric Fit: Meets the homepage requirements for a structured login/registration system.

### 🛠️ Admin Module
* *Admin Dashboard:* Provides absolute user management rights, allowing administrators to view all registered users, modify accounts, or clear inactive profiles.
* Rubric Fit: Provides clear evidence of a system operating with *more than 1 user type* and serves as our primary *User CRUD* implementation.

### 🛒 Marketplace Dashboard (User Module)
* *Home Feed:* Allows authenticated users to look through listed items, drilling down by individual categories.
* *Item Management:* Active sellers can launch new product posts (Title, Description, Price, Category), modify product details, or clear out listings from the live market view.
* Rubric Fit: Establishes our primary *Item CRUD* flow covering item creation, retrieval, updates, and final removal.

### 🔄 Shopping Cart & Connection Workflow
* *Browsing & Cart Interaction:* Users can add items to their personal lists. This flags the item's status attribute as IN_CART to temporarily claim visibility.
* *Transaction Finalization:* Item detailed lookups show a direct link to initiate contact with the seller via Call/WhatsApp. Once a verbal agreement is reached, the seller switches the item status to CLAIMED via their inventory control dashboard.
* Rubric Fit: The transaction logic maps perfectly across our system state, and the cart linkage addresses the required *many-to-many relationship structural design pattern*.