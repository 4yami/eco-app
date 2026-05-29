# Project Proposal: EcoApp (Reduce, Reuse, Recycle)

## 1. Project Overview
**Domain:** Environmental Sustainability
**Concept:** An enterprise application designed to encourage and track sustainable habits. It acts as a hybrid platform featuring a "Reuse" marketplace for second-hand items and a "Recycle" tracker that rewards users for depositing materials at registered collection centers.

This idea is highly scalable, fits the assignment requirements perfectly, and has distinct modules that make it easy to divide work among our group of 4.

---

## 2. Mapping to Assignment Requirements

### A. Client-Side (Web Component)
* **Tech Stack:** JSF/JSP (or HTML/CSS/JS via Node.js if we prefer, as per the assignment note).
* **Requirements Met:** * We will build a responsive front-end interface.
    * Includes a mandatory **Login and Registration form** for users and admins.

### B. Server-Side
* **Tech Stack:** Java (Mandatory).
* **Requirements Met:** Handles business logic (e.g., processing transactions in the reuse marketplace, calculating reward points, managing sessions).

### C. Database (Persistent Data)
* **Tech Stack:** MySQL (or JavaDB) with **Entity-JPA**.
* **Requirements Met:** Persistent storage for Users, Items, Recycling Logs, and Reward Points. JPA will map our Java objects directly to the database tables.

### D. Reusable Asset
* **Tech Stack:** EJB (Enterprise JavaBean) or Web Service.
* **Ideas:** * *Option 1 (Web Service):* A RESTful API that calculates the carbon footprint saved based on the weight of recycled materials.
    * *Option 2 (EJB):* A reusable service layer for the "Rewards Point Calculator" that can be accessed by different parts of the application.

---

## 3. Work Breakdown (Group of 4)
To ensure everyone contributes equally for the Week 13 presentation and project report, we can divide the system into four main modules:

### Module 1: User Management & Security (Member A)
* Build the Homepage, Login, and Registration interfaces.
* Implement authentication and authorization (User vs. Admin roles).
* Database tables: `User`, `Role`.

### Module 2: The "Reuse" Marketplace (Member B)
* Build interfaces for users to upload items they want to give away/sell.
* Implement search and filter functionalities for other users to find items.
* Database tables: `Item`, `Category`, `Transaction`.

### Module 3: The "Recycle" Tracker & Rewards (Member C)
* Build interfaces for users to log their recycled materials (Plastic, Glass, E-waste).
* Implement the backend logic to convert recycled weight into reward points.
* Database tables: `RecycleLog`, `RewardPoint`.

### Module 4: Admin Dashboard & Reusable Asset (Member D)
* Build the admin panel to manage users, approve marketplace listings, and manage recycling center locations.
* Develop the **Reusable Asset** (e.g., the EJB for calculating environmental impact or Web Service for nearest recycling bins).
* Database tables: `CollectionCenter`, `SystemSettings`.

---

## 4. Deliverables & Timeline
* **Week 13 Target:** Present a functioning, integrated system during class hour.
* **Project Report:** We will compile the documentation for our respective modules.
* **Source Code:** Final compilation and zipping of the project prior to the presentation day.

## 5. Next Steps for the Group
1.  **Agree on the Tech Stack:** Do we want to stick purely to JSF/JSP for the frontend, or use something else?
2.  **Database Design:** Sketch out the ERD (Entity Relationship Diagram) for our JPA entities.
3.  **Assign Roles:** Decide who takes Modules 1 through 4.
