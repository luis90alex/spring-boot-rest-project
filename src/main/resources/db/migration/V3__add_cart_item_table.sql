-- V3__add_CartItem_table.sql
-- cart_items table
CREATE TABLE cart_items
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    quantity   INT     NOT NULL DEFAULT 1,
    cart_id    BINARY(16) NOT NULL,
    product_id BIGINT  NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id)    REFERENCES cart (id)     ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
