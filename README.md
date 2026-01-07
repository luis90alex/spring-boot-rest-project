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
- CI workflow for Maven / GitHub Actions (Testcontainers + build/push to GHCR)
- **Java 17** (CI) / Spring Boot 3
- Redis caching for frequently accessed data (products, carts, sessions)
- Structured logging with Logback (JSON format) + console logs for easy monitoring


### Included Technologies & Practices
- **Spring Data JPA** (repositories + entities + queries)
- **Flyway** database migrations (idempotent migration scripts)
- **MapStruct** for DTO ↔ Entity mapping
- **Actuator** for health, metrics and info endpoints
- **Prometheus** + **Grafana** integration (monitoring + dashboards)
- **Alertmanager** for basic alerting rules (configured in docker-config/)
- **k6** load testing scripts and reports (`k6/`)
- **Testcontainers** for reliable integration tests in CI
- Docker / Docker Compose for local and full-stack runs


---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **MySQL**
- **Stripe API + Webhooks**
- **Maven**
- **Railway.app Deployment**
- **Postman (collections + tests)**
- **Docker / Docker Compose** for containerized setup
- **Prometheus + Grafana** for monitoring
- **k6** for load testing
- **Redis** (caching layer)
- **Logback** (structured logs in JSON + console output)
---

## 📦 Project Structure

```
├─ .github/
│  └─ workflows/
│     └─ ci.yml (CI: unit, integration, build & push)
├─ docker-config/
│  ├─ prometheus/
│  ├─ grafana/
│  ├─ alertmanager/
│  └─ runbooks/
├─ k6/
│  ├─ checkout-load-test.js
│  ├─ README-k6.md
│  └─ screenshots/
├─ postman/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/restlearningjourney/store/
│  │  │     ├─ admin/
│  │  │     ├─ auth/
│  │  │     ├─ carts/
│  │  │     ├─ orders/
│  │  │     ├─ payments/
│  │  │     ├─ products/
│  │  │     └─ users/
│  │  └─ resources/
│  │     ├─ application.yaml
│  │     ├─ application-dev.yaml
│  │     ├─ application-prod.yaml
│  │     └─ db/
│  │        └─ migration/         # Flyway migrations (V1__..., V2__...)
│  └─ test/
│     ├─ java/
│     │  └─ ... (unit & integration tests, containers helpers)
│     └─ resources/
├─ .env.example
├─ compose.yml
├─ Dockerfile
├─ DEMO.md
├─ pom.xml
├─ logs/
└─ README.md
```

---

## 📘 Demo Guide (curl + full API flow)

If you want to reproduce the full API flow (login → products → cart → checkout → Stripe webhook),
see the dedicated guide:

👉 **[DEMO.md](./DEMO.md)**

This includes step-by-step curl commands, sample requests, expected responses, Stripe test flow,
and troubleshooting tips used for portfolio demos and interviews.

---

## ▶️ How to Run the Project Locally

You can run the application in **two ways** (both supported): **(A)** development workflow using Java + local MySQL plus monitoring containers, or **(B)** full containerized approach.

> **Tip:** During active development I typically use **Mode A** (Java + local MySQL + monitoring containers). Both options are documented below so reviewers and interviewers can reproduce the demo quickly.

### 0. Clone the repository

```bash
git clone https://github.com/luis90alex/spring-boot-rest-project
cd spring-boot-rest-project
```

---

### 1. Create `.env` file

This repo includes an `.env.example` file. Create `.env` by copying and filling the values.

Example `.env.example` (do **not** commit secrets):

```
JWT_SECRET=YOUR_JWT_SECRET
STRIPE_SECRET_KEY=YOUR_STRIPE_SECRET
STRIPE_WEBHOOK_SECRET_KEY=YOUR_WEBHOOK_SECRET
MYSQL_ROOT_PASSWORD=ROOT_PASSWORD_ONLY_USED_IN_MYSQL_CONTAINER
MYSQL_DATABASE=DATABASE_NAME_ONLY_USED_IN_MYSQL_CONTAINER
MYSQL_USER=USERNAME
MYSQL_PASSWORD=USER_PASSWORD
```

**Notes:**

- `MYSQL_ROOT_PASSWORD` and `MYSQL_DATABASE` are **only used** if you run MySQL **inside Docker containers** (mode B). If you run MySQL locally, use your local DB credentials and set `SPRING_DATASOURCE_URL` or the `application-dev.yml` properties accordingly.
- Keep all real secrets out of the repo and use your CI secrets store for GitHub Actions.

---

### Mode A — Java + local MySQL + monitoring containers (recommended for dev)

1. Ensure a local MySQL is running and create the database (example):

```bash
mysql -u root -p
CREATE DATABASE store_api;
```

2. Create `.env` from `.env.example` and set `MYSQL_USER` and `MYSQL_PASSWORD` to your local DB user (or leave defaults and use `application-dev.yml` local url).

3. Run the application locally (Maven wrapper):

```bash
./mvnw spring-boot:run
```

4. Start monitoring stack (Prometheus, Grafana, Alertmanager) in containers:

```bash
docker compose up -d
```

5. Access:
- App: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (default admin password: `admin` in the dev compose)
- Alertmanager: `http://localhost:9093`

---

### Mode B — All in Docker (app + MySQL + Prometheus + Grafana + Alertmanager + Redis)

This mode runs everything as containers. Use this to reproduce the full stack exactly as in the demo environment.
Uncomment all services in dockerfile and after that: 
1. Build and run:

```bash
docker compose up --build
```

2. The compose file will start the Java app image (built from `Dockerfile`), MySQL container (if enabled in `compose.yml`), and monitoring containers. Ports are exposed on the host so you can reach the services at the same URLs noted above.

> Note: if you prefer to run only a subset of the services you can pass the service names to `docker compose up` (for example `docker compose up -d prometheus grafana` to run only the monitoring stack).

---

## 🌍 Deployment (Railway)

Production deployment is available here (example):

👉 **https://spring-boot-rest-project-production.up.railway.app**

Railway uses these application environment variables (example):

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
 ├─ store-api.postman_collection.json
 ├─ environment.dev.json
 └─ environment.prod.json
```

- **Dev environment example**:
```json
{
  "baseUrl": "http://localhost:8080",
  "ENVIRONMENT": "DEV"
}
```

- **Prod environment example**:
```json
{
  "baseUrl": "https://spring-boot-rest-project-production.up.railway.app",
  "ENVIRONMENT": "PROD"
}
```

- Script to store `accessToken` in Postman collection run scripts:
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

You will receive a webhook secret (example):

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

## 📊 Monitoring & Load Testing

This project exposes useful observability endpoints and includes load testing scripts.

- **Actuator** endpoints are exposed under `/actuator` (config in `application*.yml`)
- **Prometheus** scrapes the Actuator `/actuator/prometheus` endpoint
- **Grafana** dashboards are provisioned via `docker-config/grafana/` and connected to Prometheus
- **Alertmanager** is configured to receive alerts (see `docker-config/alertmanager/alertmanager.yml`)
- **k6** scripts for load testing are stored in `k6/`:
  - `k6/checkout-load-test.js`
  - `k6/README-k6.md`
  - Results and screenshots: `k6/screenshots/`
- **Email Notifications**. Uses **SMTP** for email delivery. Mailtrap is recommended for testing 
(free and easy to set up)

Quick commands:

- Start only monitoring containers (Mode A dev):
```bash
docker compose up -d prometheus grafana alertmanager
```

- View Prometheus: `http://localhost:9090`  
- View Grafana: `http://localhost:3000` (default admin: `admin`)  
- View Alertmanager: `http://localhost:9093`

### Alerting

This project includes two production-style Prometheus alerts related to the checkout flow:

- `CheckoutHighErrorRate` — fires when the checkout endpoint has an unusually high error rate (>5%).
- `CheckoutHighLatencyP95` — fires when the 95th percentile latency for checkout is above the defined threshold.
-  `TestAlert` - firing testing alarm (commented by default)

Alerts are sent by **Alertmanager** via SMTP using Mailtrap credentials (used for development/testing).
Example of the section email configuration (docker-config/alertmanager/alertmanager.yml):
```yaml
receivers:
  - name: "email-alerts"
    email_configs:
      - to: "your-email@example.com"
        from: "hello@demomailtrap.co"
        smarthost: "live.smtp.mailtrap.io:587"
        auth_username: "api"
        auth_password: "YOUR_API_TOKEN"
        require_tls: true
```

### Runbooks
Runbooks are included in docker-config/runbooks/

Example: checkout_alerts.md explains the steps to follow when CheckoutHighErrorRate
or CheckoutHighLatencyP95 alerts fire

---

## 🧹 Code Quality & CI

This repo includes a GitHub Actions workflow for CI:

- File: `.github/workflows/ci.yml` (active or disabled file present)
- Jobs include:
  - Build & unit tests (JUnit / Maven)
  - Integration tests using Testcontainers (mysql in container) or
  an integration test using H2 database
  - Build & push Docker image to GHCR (if enabled)
  - Upload test reports artifacts (surefire / failsafe)

**Notes / hints**:
- The integration job expects `JWT_SECRET` in the runner secrets (configured as `secrets.JWT_SECRET`).

CI snippet highlights:

- Java version in CI: **17** (uses `actions/setup-java@v5`, `distribution: temurin`, `java-version: '17'`)  
- Image publishing: the workflow logs into `ghcr.io` and pushes tags based on git refs/commit SHA.  
- Optional: a Trivy scan step is included for image vulnerability scanning (you can enable `fail-on-high` if you want CI to fail on high vulnerabilities).

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
