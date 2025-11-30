# Spring Boot REST E-commerce API

A full-featured backend REST API for a simple e-commerce system built using **Spring Boot 3**, **JWT authentication**, **MySQL**, **Stripe payments**, and clean modular architecture.  
This project is designed as a real-world portfolio backend suitable for showcasing professional backend development skills.

---

## 🚀 Features

- User registration, login, refresh token + JWT-based authentication  
- CRUD operations for **Users** and **Products**  
- Full shopping cart system  
- Order creation & retrieval  
- Stripe Checkout integration with webhook validation  
- Admin endpoint example  
- Postman collection and environments  
- Environment-based config (dev/prod)  
- CI workflow for Maven / Java 21  

---

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Security + JWT**
- **MySQL**
- **Stripe API + Webhooks**
- **Maven**
- **Railway.app Deployment**
- **Postman (collections + tests)**

---

## 📦 Project Structure

```
src/main/java
 ├─ controllers/
 ├─ services/
 ├─ repositories/
 ├─ exceptions/
 ├─ dto/
 └─ security/
```

---

## ▶️ How to Run the Project Locally

### 1. Clone the repository

```bash
git clone https://github.com/luis90alex/spring-boot-rest-project
cd spring-boot-rest-project
```

---

### 2. Create `.env` file

You have an env.example file
```env
JWT_SECRET=your-secret-here
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET_KEY=whsec_...
```

---

### 3. Start MySQL locally

```bash
mysql -u root -p
CREATE DATABASE store;
```

---

### 4. Run with Maven Wrapper

```bash
./mvnw spring-boot:run
```

Server runs at:  
👉 `http://localhost:8080`

---

## 🌍 Deployment (Railway)

Production deployment is available here:

👉 **https://spring-boot-rest-project-production.up.railway.app**

Railway uses these application environment variables:

```
JWT_SECRET
SPRING_DATASOURCE_URL
SPRING_PROFILES_ACTIVE
STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET_KEY
```

### ⚠️ About MySQL on Railway

You must:

1. Create a **MySQL database** in Railway  
2. Link it to your backend service  
3. Railway will generate a variable such as:

```
MYSQL_URL = mysql://root:password@containers-id.railway.app:3306/railway
```

4. Convert it into Spring Boot format (add `jdbc:`):

```
SPRING_DATASOURCE_URL = jdbc:mysql://root:password@containers-id.railway.app:3306/railway
```

---

## 🧪 Postman Collection

This repository includes:

```
postman/
 ├─ collection.json
 ├─ environment.dev.json
 └─ environment.prod.json
```

### Dev environment example

```json
{
  "baseUrl": "http://localhost:8080",
  "ENVIRONMENT": "DEV"
}
```

### Prod environment example

```json
{
  "baseUrl": "https://spring-boot-rest-project-production.up.railway.app",
  "ENVIRONMENT": "PROD"
}
```

### Script: Store the accessToken

```javascript
var json = pm.response.json();
pm.collectionVariables.set("accessToken", json.token);
```

---

## 💳 Stripe Integration (Local)

Requires a Stripe account — test mode is free.

### 1. Install Stripe CLI

```bash
brew install stripe/stripe-cli/stripe
```

### 2. Login

```bash
stripe login
```

### 3. Connect Stripe to your local server

```bash
stripe listen --forward-to http://localhost:8080/checkout/webhook
```

You will receive a webhook secret like:

```
whsec_123456
```

Put this value in `.env`:

```
STRIPE_WEBHOOK_SECRET_KEY=whsec_123456
```

### 4. Trigger a test event

```bash
stripe trigger payment_intent.succeeded
```

---

## 📘 API Overview (Representative Endpoints)

Below is a curated list of the *most important* endpoints.

---

### 🔐 Auth

| Method | Endpoint       | Description         |
|--------|----------------|---------------------|
| POST   | `/auth/login`  | Login & get JWT     |
| POST   | `/auth/refresh`| Refresh token       |
| GET    | `/auth/me`     | Get current user    |

---

### 👤 Users

| Method | Endpoint        | Description    |
|--------|-----------------|----------------|
| GET    | `/users/{id}`   | Get user       |
| PUT    | `/users/{id}`   | Update user    |
| DELETE | `/users/{id}`   | Delete user    |
| POST   | `/users`        | Create user    |

---

### 🛍️ Products

| Method | Endpoint         | Description       |
|--------|------------------|-------------------|
| GET    | `/products`      | List products     |
| POST   | `/products`      | Create product    |
| GET    | `/products/{id}` | Get product       |

---

### 🛒 Cart

| Method | Endpoint                                  | Description      |
|--------|---------------------------------------------|------------------|
| POST   | `/carts`                                   | Create cart      |
| GET    | `/carts/{cartId}`                          | Get cart         |
| POST   | `/carts/{cartId}/items`                    | Add product      |
| PUT    | `/carts/{cartId}/items/{productId}`        | Update quantity  |
| DELETE | `/carts/{cartId}/items/{productId}`        | Remove product   |

---

### 🧾 Orders

| Method | Endpoint         | Description  |
|--------|------------------|--------------|
| GET    | `/orders`        | List orders  |
| GET    | `/orders/{id}`   | Get order    |

---

### 💳 Checkout (Stripe)

| Method | Endpoint               | Description         |
|--------|------------------------|---------------------|
| POST   | `/checkout`            | Create session      |
| POST   | `/checkout/webhook`    | Stripe webhook      |

---

### 🔧 Admin

| Method | Endpoint        | Description          |
|--------|-----------------|----------------------|
| GET    | `/admin/hello`  | Admin-only endpoint  |

---

## 🧹 Code Quality & CI

Includes GitHub Actions workflow:

- Uses **JDK 21**
- Runs `mvn -B verify`
- Optional: Newman to run Postman tests

File:  
`.github/workflows/ci.yml`

---

## 📄 License

This project uses the **MIT License**.  
You are free to use, modify, and distribute it.

---

## 💬 Contact

If you have questions, suggestions or want to collaborate, feel free to open an issue or reach out directly.

**Luis Farfan**  
Backend Developer · Java & Spring Specialist  
📍 Barcelona

- GitHub: https://github.com/luis90alex
- LinkedIn: Luis Farfan
- Email: [luis90alex@gmail.com](mailto:luis90alex@gmail.com)

I welcome feedback, contributions and collaboration requests — drop a message and I’ll get back to you.

---

## ⭐ Enjoying the project?

If this repository helped you:

- Please consider giving it a **⭐ on GitHub** — it helps the project reach more people.
- Found a bug or improvement? Open an **Issue**.
- Want to contribute? Send a **PR** — add tests and a short description of the change.

I’m also working on more Spring Boot and architecture-focused projects — follow me on GitHub or LinkedIn to stay updated. Thanks for checking this project out!
