-- V3__add_cart_item_table_h2.sql
CREATE TABLE cart_items (
                            id         BIGINT AUTO_INCREMENT NOT NULL,
                            quantity   INT NOT NULL DEFAULT 1,
                            cart_id    CHAR(36) NOT NULL,   -- corresponde con cart.id CHAR(36)
                            product_id BIGINT NOT NULL,
                            CONSTRAINT pk_cart_items PRIMARY KEY (id),
                            CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
                            CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
                            CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- índices recomendados para consultas
CREATE INDEX idx_cart_items_cart ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product ON cart_items (product_id);