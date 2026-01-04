import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export let errorRate = new Rate('errors');

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

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USER_EMAIL = __ENV.USER_EMAIL || 'user_l@domain.com';
const USER_PASSWORD = __ENV.USER_PASSWORD || 'passABC';
const PRODUCT_IDS = (__ENV.PRODUCT_IDS || '3').split(',').map(s => s.trim()).filter(s => s);

console.log(`BASE_URL ID: ${BASE_URL}`);
console.log(`USER_EMAIL ID: ${USER_EMAIL}`);
console.log(`USER_PASSWORD ID: ${USER_PASSWORD}`);
console.log(`PRODUCT_IDS ID: ${PRODUCT_IDS}`);

export function setup() {
    console.log('=== LOGIN ===');
    let loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        email: USER_EMAIL,
        password: USER_PASSWORD
    }), { headers: { 'Content-Type': 'application/json' }});

    check(loginRes, {
        'login status 200': (r) => r.status === 200,
        'login returns token': (r) => r.json('token') !== undefined
    }) || errorRate.add(1);

    const token = loginRes.json('token');
    console.log(`Login token: ${token.substring(0,20)}...`); // no mostrar todo el token
    return { token };
}

export default function (data) {
    const authHeaders = { Authorization: `Bearer ${data.token}`, 'Content-Type': 'application/json' };

    console.log('=== CREATE CART ===');
    let cartRes = http.post(`${BASE_URL}/carts`, null, { headers: authHeaders });
    const cartOk = check(cartRes, {
        'cart created': (r) => r.status === 201 && r.json('id') !== undefined
    });
    if (!cartOk) { errorRate.add(1); sleep(1); return; }

    const cartId = cartRes.json('id');
    console.log(`Cart ID: ${cartId}`);

    console.log('=== ADD ITEMS ===');
    const toAdd = PRODUCT_IDS.length > 1 ? PRODUCT_IDS.slice(0, 2) : PRODUCT_IDS;
    toAdd.forEach(productId => {
        let itemRes = http.post(`${BASE_URL}/carts/${cartId}/items`, JSON.stringify({ productId: Number(productId) }), { headers: authHeaders });
        check(itemRes, {
            'item added': (r) => r.status === 201 || r.status === 200,
            'item has product': (r) => r.json('product') !== undefined
        }) || errorRate.add(1);

        console.log(`Added product ${productId} to cart`);
        sleep(0.2);
    });

    console.log('=== CHECKOUT ===');
    let checkoutRes = http.post(`${BASE_URL}/checkout`, JSON.stringify({ cartId }), { headers: authHeaders });
    check(checkoutRes, {
        'checkout success': (r) => r.status === 200 && r.json('checkoutUrl') !== undefined
    }) || errorRate.add(1);

    console.log(`Checkout URL: ${checkoutRes.json('checkoutUrl')}`);
    sleep(1);
}