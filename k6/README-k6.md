# Checkout Load Test (k6)

This load test simulates the complete **checkout flow** of an e-commerce
application using **k6**.

The goal is to evaluate: - Latency - Throughput - Error rate - System
behavior under progressive load

------------------------------------------------------------------------

## 🔁 Simulated Flow

Each Virtual User (VU) executes the following steps:

1.  **Login** (executed once in `setup`)
2.  Create a cart
3.  Add one or more products to the cart
4.  Perform checkout
5.  Think time (pause)

The login is executed **only once**, and the authentication token is
shared across all VUs.

------------------------------------------------------------------------

## 📈 Load Model (Stages)

The test is divided into **three stages**:

``` js
stages: [
  { duration: '15s', target: 10 }, // ramp-up
  { duration: '2m', target: 10 },  // steady
  { duration: '15s', target: 0 },  // ramp-down
]
```

### Stage Description

  Stage       Description
  ----------- ----------------------------------------------
  Ramp-up     Gradually increases VUs from 0 to the target
  Steady      Maintains a constant number of VUs
  Ramp-down   Gradually decreases VUs to 0

This simulates realistic user traffic entering and leaving the system.

------------------------------------------------------------------------

## 🌍 Environment Variables

The script can be configured using environment variables:

| Variable        | Description                     | Default                 |
|-----------------|---------------------------------|-------------------------|
| `BASE_URL`      | Base API URL                    | `http://localhost:8080` |
| `USER_EMAIL`    | Existing user email             | `user_l@domain.com`     |
| `USER_PASSWORD` | User password                   | `passABC`               |
| `PRODUCT_IDS`   | Product IDs (comma-separated)   | `3`                     |
| `VUS`           | Maximum number of Virtual Users | `10`                    |
| `RAMP_UP`       | Ramp-up duration                | `15s`                   |
| `STEADY`        | Steady phase duration           | `2m`                    |
| `RAMP_DOWN`     | Ramp-down duration              | `15s`                   |

⚠️ **Important**: The user must already exist in the system.

------------------------------------------------------------------------

## ▶️ How to Run the Test
Make sure you run these commands from the directory where checkout-load-test.js 
is located, or provide the full path to the script.
### Basic execution

``` bash
k6 run checkout-load-test.js
```

### Execution with logs and exported summary

``` bash
k6 run --summary-export summary.json checkout-load-test.js 2>&1 | Tee-Object -FilePath k6.log
```
------------------------------------------------------------------------

## 📤 Generated Outputs

  File             Description
  ---------------- -------------------------------------------------
  `k6.log`         Execution logs (errors, VU status)
  `summary.json`   Full test metrics (latency, errors, throughput)

The `summary.json` file can be used for: - Post-test analysis -
Comparing multiple runs - Reporting results

------------------------------------------------------------------------

## Load Test Results

The following results were obtained using the `checkout-load-test.js` script with **10 Virtual Users (VUs)**.  
The test ran in three phases: ramp-up (15s), steady (2m), and ramp-down (15s), simulating realistic checkout traffic.

### Requests per Second (Req/s)

This panel shows the number of checkout requests per second processed by the API during the test.  
![Requests per second](k6/screenshots/k6_req_per_sec_2026-01-05.png)

**Observation:**
- Peak throughput reached ~6 requests/sec.
- Throughput smoothly increased during ramp-up, stayed stable during the steady phase, and decreased during ramp-down.

---

### Latency (p50 / p95)

This panel shows the response times (latency) percentiles for the checkout endpoint:

- **p50** – median latency
- **p95** – 95th percentile latency

![Latency p50/p95](k6/screenshots/k6_latency_p50_p95_2026-01-05.png)

**Observation:**
- Median latency (p50) stayed around **~500 ms**.
- 95th percentile latency (p95) peaked at **~594 ms**, well below the 3-second target threshold (`p(95)<3000ms`).

---

### Notes

- These results were generated with **10 VUs**.
- Metrics were collected via **Micrometer → Prometheus → Grafana**.
- Error rate was minimal during the test (`<5%`).
- For reproducibility, see the script [`checkout-load-test.js`](./checkout-load-test.js) and the environment variable configuration.

## 🚨 Important Notes

-   This test **creates real orders** in the database.
-   It should be executed only against test environments.
-   Before running:
    -   the application must be running
    -   the database may need to be cleaned (optional)
-   After running:
    -   a cleaning sql file can be used (truncate_tables_k6_test.sql)
    -   be careful with that file it truncates order_items, orders, 
    cart and cart_items tables
    -   logs are generated to analyse them if necessary

------------------------------------------------------------------------

## 🧠 Best Practices

-   Always use ramp-up and ramp-down
-   Increase load progressively
-   Compare results before and after changes
-   Avoid running load tests directly on production

------------------------------------------------------------------------

## 🏁 Conclusion

This test validates system performance under realistic load and is part
of the **Observability & Performance** phase of the project.
