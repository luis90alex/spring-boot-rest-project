-- V5__add_orders_and_order_items_h2.sql

CREATE TABLE orders (
                        id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        status      VARCHAR(20) NOT NULL,
                        created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total_price DECIMAL(10, 2) NOT NULL,
                        CONSTRAINT fk_orders_users_id FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE order_items (
                             id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id    BIGINT NOT NULL,
                             product_id  BIGINT NOT NULL,
                             unit_price  DECIMAL(10, 2) NOT NULL,
                             quantity    INT NOT NULL,
                             total_price DECIMAL(10, 2) NOT NULL,
                             CONSTRAINT fk_order_items_orders_id FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT fk_order_items_products_id FOREIGN KEY (product_id) REFERENCES products(id)
);
