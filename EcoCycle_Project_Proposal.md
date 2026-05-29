# Project Proposal: EcoApp (Reduce & Reuse Tracker)

## 1. Project Overview
**Domain:** Environmental Sustainability  
**Concept:** An enterprise application designed to promote sustainable habits through two core features: a **"Reuse" marketplace** for giving away or selling second-hand items, and a **"Recycle" tracker** where users log their dropped-off materials (plastic, glass, paper) at local collection centers to view their personal environmental impact.

By cutting out points and rewards, this system stays lean, minimizes database complexity, and allows a group of 4 to cleanly divide modules for a successful Week 13 live system presentation.

---

## 2. Mapping to Assignment Requirements

### A. Client-Side (Web Component)
* **Tech Stack:** JSF/JSP (or HTML/CSS/JS via Node.js as permitted by the lecturer's note).
* **Requirements Met:** A clean, responsive front-end interface featuring a mandatory **Login and Registration form** distinguishing between multiple user roles (Users and Admins).

### B. Server-Side
* **Tech Stack:** Java (Mandatory).
* **Requirements Met:** Handles core business logic, session routing, and data processing (e.g., filtering marketplace items, calculating total carbon offset metrics).

### C. Database (Persistent Data)
* **Tech Stack:** MySQL / JavaDB with **Entity-JPA**.
* **Requirements Met:** Persistent storage for application data. JPA will map Java objects directly to relational database tables, ensuring we can model the required relationship designs (including an intentional Many-to-Many setup).

### D. Reusable Asset
* **Tech Stack:** EJB (Enterprise JavaBean) or Shared Library / Web Service.
* **Implementation:** A standalone **Carbon Footprint Calculator** component. When a recycling weight is logged, this component processes the material type and weight through a fixed environmental impact algorithm to return the total carbon saved.

---

## 3. Work Breakdown (Group of 4)

To ensure equal contribution for the presentation and documentation, the system is divided into four highly focused modules:

### Module 1: User Management & Authentication (Member A)
* Build the Homepage, Login, and Registration front-end forms.
* Implement session management and role-based access control (User vs. Admin).
* **Database Entities:** `User`, `Role`.

### Module 2: The "Reuse" Marketplace (Member B)
* Create interfaces for users to upload items they want to give away or sell.
* Implement basic CRUD operations (Create, Read, Update, Delete) for managing item listings.
* **Database Entities:** `Item`, `Category` (modeled cleanly to match the sample brief layout).

### Module 3: The "Recycle" Contribution Tracker (Member C)
* Build the form for users to log their material drop-offs (selecting from a list of approved centers, picking material types, and inputting weights).
* Display a user-specific "Contribution History" showing their logged items and the resulting carbon offset metrics.
* **Database Entities:** `RecycleLog`.

### Module 4: Admin Dashboard & Reusable Asset (Member D)
* Build a simple admin dashboard to manage system assets (adding/removing participating `CollectionCenter` locations).
* Develop the backend **Reusable Asset** (EJB or Web Service) that performs the calculation logic for Member C's module.
* **Database Entities:** `CollectionCenter`.

---

## 4. Deliverables & Timeline Checklist
* **Week 13 Target:** Deliver and present a fully functioning, integrated application during the designated class hour.
* **The "Same Day" Rule:** Ensure that the final **Project Report** and zipped **Source Code** are submitted via the portal on the *exact same day* as our presentation.
* **Report Formatting:** Maintain formatting rules throughout the report writing process:
    * **Font:** Arial or Calibri (Size 11, 1.5 line spacing).
    * **Page Numbers:** Enabled across all pages.
    * **Diagrams:** Include all 5 required UML visuals (Use Case, Component, Deployment, Package, and Class Diagrams) alongside the Data Model.