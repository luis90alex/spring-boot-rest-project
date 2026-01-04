# Checkout Alerts Runbook

## CheckoutHighErrorRate
**Severity:** critical

### What does this mean?
More than **5% of `/checkout` requests are failing** for over 1 minute.

### Impact
Users cannot complete purchases, causing **direct revenue loss**.

### What to check first
1. Grafana dashboard → *Checkout Error Rate*
2. Application logs (`CheckoutService`)
3. Payment gateway availability (Stripe)

### Immediate actions
- Check if the issue is external (payment provider)
- Distinguish between 4xx and 5xx errors
- Roll back recent deployments if applicable


---

## CheckoutHighLatencyP95
**Severity:** warning

### What does this mean?
The **95th percentile checkout latency exceeds 3 seconds** for more than 2 minutes.

### Impact
Slow checkout experience leading to **user frustration and abandonment**.

### What to check first
1. Grafana dashboard → *Checkout Latency (p50 / p95)*
2. CPU and memory usage
3. Database and payment gateway latency

### Interpreting p50 vs p95
- **p50 and p95 are both high**
  - Overall system slowness
  - Check CPU, database performance, thread pools

- **p50 is normal but p95 is high**
  - Only a subset of requests is slow
  - Common causes: external dependencies (payment gateway), locks, GC pauses

### Immediate actions
- Verify external payment latency
- Inspect database locks or slow queries
- Scale the application if under high load
