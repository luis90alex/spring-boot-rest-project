# DEMO — Quick scripted demo (3–5 minutes)

**Goal:** show a short, repeatable demo of the core flow: auth → add items to cart → checkout → Stripe webhook → verify order.  
Run locally (`http://localhost:8080`) or against staging: `https://spring-boot-rest-project-production.up.railway.app`.

---

## Preconditions (before the demo)

1. Start the app locally:
   ```bash
   ./mvnw spring-boot:run
   ```
   or use the Railway staging URL.

2. If running locally, create a local MySQL DB and set `.env` (see README). Start MySQL and apply Flyway migrations automatically when the app starts.

3. Optional but recommended (for webhook testing): install Stripe CLI and log in:
   ```bash
   stripe login
   ```

4. jq is used in the examples to pretty-print JSON. Install it if you don't have it:
    ```bash
    # macOS
    brew install jq
    # Debian/Ubuntu
    sudo apt-get install -y jq
    ```

5. Import Postman collection (optional): `postman/collection.json` and `postman/environment.dev.json`. Set `baseUrl` to `http://localhost:8080` or to the staging URL.

---

## Demo script (steps)

### Step 0 — Quick status check
Show the health endpoint:
```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```
Show Swagger if you want:
- `http://localhost:8080/swagger-ui.html`

---

### Step 1 — Register (create user) *(if account doesn't exist)*
Use the demo account or create one.

**Example curl (register):**
```bash
curl -s -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@store.test",
    "password": "DemoPass123",
    "name": "Demo User"
  }' | jq
```
**Expected (HTTP 201)**: JSON user object (id, email, ...).

> If the demo user already exists, skip to login.

---

### Step 2 — Login and capture token
**curl (login):**
```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@store.test","password":"DemoPass123"}' | jq
```
**Expected response** (example):
```json
{
  "token": "eyJhbGciOi..."
}
```

**Save token (bash example):**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@store.test","password":"DemoPass123"}' | jq -r '.token')
echo $TOKEN  # verify
```

In Postman the login Test script should store `accessToken` automatically.

---

### Step 3 — List products (show available items)
```bash
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/products" | jq
```
Show a list of the products stored in the database.

---

### Step 4 — Create a cart (if your flow requires explicit cart creation)
```bash
curl -s -X POST http://localhost:8080/carts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' | jq
```
**Expected:** cart object with `cartId`.

(If your API auto-creates carts on first add, you can skip explicit create.)

---

### Step 5 — Add product(s) to cart
```bash
# Replace <cartId> and <productId> with real values from previous steps
curl -s -X POST http://localhost:8080/carts/<cartId>/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "<productId>",
    "quantity": 1
  }' | jq
```

Show the `GET /carts/<cartId>` response with items and subtotal.

---

### Step 6 — Start checkout (create Stripe session)
```bash
curl -s -X POST http://localhost:8080/checkout \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": "<cartId>"
  }' | jq
```

**Expected response (real output from this project):**
```bash
{
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_12345...",
  "orderId": 1
}
```
This checkoutUrl is a real Stripe Checkout session URL. In a real application the frontend 
would redirect the user to this page; for the demo you can open it in a browser 
(optional) to show Stripe Checkout. 

---

### Step 7 — Prepare webhook listener (local only)
If local, open a new terminal and forward Stripe events to your webhook endpoint:
```bash
stripe listen --forward-to http://localhost:8080/checkout/webhook
```
Stripe CLI prints a `Signing secret (whsec_...)`. Paste it into your `.env` (or set it in Railway if using staging):
```
STRIPE_WEBHOOK_SECRET_KEY=whsec_...
```

---

### Step 8 — Trigger a Stripe test event
In another terminal:
```bash
stripe trigger payment_intent.succeeded
```
This sends a test `payment_intent.succeeded` event to your webhook. The app should validate the signature and process the event.

**Check webhook processing:**
- Logs: show application logs (console) — find webhook event and processing result.
- Orders endpoint: verify the order status changed to `PAID` (or equivalent).

```bash
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/orders | jq
```

Show the created/updated order.

---

## What to highlight during the demo (talking points)

- **Security:** JWT auth + server-side signature validation for Stripe webhooks; idempotency by storing processed `event.id`.
- **Reproducibility:** Postman collection + README and demo steps allow anyone to reproduce the flow.
- **Migrations:** Flyway ensures DB schema is versioned and reproducible across environments.
- **Observability:** show `/actuator/health` and mention logs/metrics (explain you can show metrics/trace if available).
- **Testing & CI:** mention forthcoming Testcontainers tests and CI pipeline (badge in README).

---

## Expected timings (for interview)

- Health + Swagger quick show: 20–30s  
- Login + list products + add to cart: 60–90s  
- Checkout + trigger webhook + verify order: 60–90s  
- Wrap-up + questions: 30–60s  
**Total:** ~4 minutes

---

## Troubleshooting (quick tips)

- **401 Unauthorized:** ensure `TOKEN` is set and included as `Authorization: Bearer <token>`.
- **Webhook signature invalid:** re-run `stripe listen` and update `STRIPE_WEBHOOK_SECRET_KEY` with the printed `whsec_...`.
- **DB errors:** check `SPRING_DATASOURCE_URL` and that MySQL is running; check Flyway logs for migration errors.
- **Missing product/cart IDs:** list products first (`GET /products`) and use an existing product id.

---

## Optional Postman flow

1. Import `postman/collection.json` and `postman/environment.dev.json`.
2. Select environment `DEV` (ensure `baseUrl` equals `http://localhost:8080`).
3. Run `Auth -> Login` request; the Test script stores `accessToken`.
4. Run `Products -> GET /products`.
5. Run Cart requests to add items.
6. Run `Checkout -> POST /checkout`.
7. Trigger Stripe event via CLI and check `Orders -> GET /orders`.

---

## Notes for remote demo (using Railway)

If you use the Railway staging URL:

- Use the same flow but with `baseUrl` = `https://spring-boot-rest-project-production.up.railway.app`.
- You cannot forward Stripe CLI to Railway unless you configure a public webhook endpoint in Stripe dashboard; instead, 
test with a staging Stripe webhook configured in Stripe dashboard pointing to 
`/checkout/webhook` and use Stripe test events in the dashboard.

---

## Wrap-up script (bash) — full quick run (local)

> Use for repeated demos (replace `<productId>` placeholder first).

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"demo@store.test","password":"DemoPass123"}' | jq -r '.token')
PRODUCT_ID=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/products | jq -r '.[0].id')
CART_ID=$(curl -s -X POST http://localhost:8080/carts -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{}' | jq -r '.id')
curl -s -X POST http://localhost:8080/carts/$CART_ID/items -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"productId\":\"$PRODUCT_ID\",\"quantity\":1}" | jq
curl -s -X POST http://localhost:8080/checkout -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"cartId\":\"$CART_ID\",\"successUrl\":\"https://example.com/success\",\"cancelUrl\":\"https://example.com/cancel\"}" | jq
# trigger stripe event in separate terminal:
# stripe trigger payment_intent.succeeded
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/orders | jq
```
