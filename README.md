# Marketplace Backend (Spring Boot + JWT + MySQL)

Requirements:
- Java 17
- Maven
- MySQL server

1) Create MySQL database:
   CREATE DATABASE marketplace_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

2) Update src/main/resources/application.properties with your MySQL credentials and a secure jwt.secret.

3) Build & run:
   mvn spring-boot:run

4) Endpoints:
Auth:
- POST /api/auth/register
  Body: { "name":"...", "email":"...", "password":"...", "role":"USER|VENDOR|ADMIN", "shopName":"..." }
- POST /api/auth/login
  Body: { "email":"...", "password":"..." }
  -> returns { "token": "..." }

Use returned token for protected endpoints:
Authorization: Bearer <token>

Examples:
- GET /api/products
- POST /api/products (requires VENDOR or ADMIN) with header Authorization
- POST /api/orders (authenticated USER) with header Authorization and body:
  {
    "items": [
      { "productId": 1, "quantity": 2 }
    ]
  }
- POST /api/categories (ADMIN to create categories)

Notes:
- Change jwt.secret to a secure random string for production.
- Passwords are stored hashed with BCrypt.
- Improve validation and exception handling for production use.
