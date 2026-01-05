import http from 'k6/http'; //import http methods like get,post,put,delete...
import { check, sleep } from 'k6';// import check and sleep methods to verify and simulate think time
import { Rate } from 'k6/metrics';
import exec from 'k6/execution';// import exec to obtain execution data

export let errorRate = new Rate('errors');
// 3 stages are defined. At the beginning there are 0 VU that increments gradually until reaching the target number.
// This is the ramp-up stage. After that ,during steady stage they use the app to do the checkout and finally
// in the ramp-down stage, VU is reducing until arriving to 0 VU.

export let options = {
    stages: [
        { duration: __ENV.RAMP_UP || '15s', target: Number(__ENV.VUS || 10) },
        { duration: __ENV.STEADY || '2m', target: Number(__ENV.VUS || 10) },
        { duration: __ENV.RAMP_DOWN || '15s', target: 0 },
    ],
    thresholds: {
        errors: ['rate<0.05'],
        http_req_duration: ['p(95)<3000'],
    },
};

// Environment variables can be used or default values
// Remember to use an already registered user. If the user is not registered, the script won't work
// All the details in readme-k6.md
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USER_EMAIL = __ENV.USER_EMAIL || 'user_l@domain.com';
const USER_PASSWORD = __ENV.USER_PASSWORD || 'passABC';
const PRODUCT_IDS = (__ENV.PRODUCT_IDS || '3')
    .split(',')
    .map(s => s.trim())
    .filter(Boolean);

/* ---------------- SETUP ---------------- */

export function setup() {
    let res = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({ email: USER_EMAIL, password: USER_PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    const ok = check(res, {
        'login status 200': r => r.status === 200,
        'login returns token': r => r.json('token') !== undefined,
    });

    if (!ok) {
        console.error(`[SETUP] Login failed (status=${res.status})`);
        errorRate.add(1);
        return {};
    }

    const token = res.json('token');
    console.log(`[SETUP] Login OK, token=${token.substring(0, 20)}...`);
    return { token };
}

/* ---------------- DEFAULT ---------------- */

export default function (data) {
    const vu = exec.vu.idInTest;
    const headers = {
        Authorization: `Bearer ${data.token}`,
        'Content-Type': 'application/json',
    };

    // Create cart
    let cartRes = http.post(`${BASE_URL}/carts`, null, { headers });
    if (!check(cartRes, { 'cart created': r => r.status === 201 })) {
        console.error(`[VU ${vu}] Cart creation failed`);
        errorRate.add(1);
        return;
    }

    const cartId = cartRes.json('id');

    // Add items
    for (const productId of PRODUCT_IDS) {
        let itemRes = http.post(
            `${BASE_URL}/carts/${cartId}/items`,
            JSON.stringify({ productId: Number(productId) }),
            { headers }
        );

        if (!check(itemRes, { 'item added': r => r.status < 300 })) {
            console.error(
                `[VU ${vu}] Failed adding product ${productId} to cart ${cartId}`
            );
            errorRate.add(1);
            return;
        }
    }

    // Checkout
    let checkoutRes = http.post(
        `${BASE_URL}/checkout`,
        JSON.stringify({ cartId }),
        { headers }
    );

    if (!check(checkoutRes, { 'checkout success': r => r.status === 200 })) {
        console.error(`[VU ${vu}] Checkout failed for cart ${cartId}`);
        errorRate.add(1);
        return;
    }

    console.log(`[VU ${vu}] Checkout OK (cart=${cartId})`);
    sleep(1);
}