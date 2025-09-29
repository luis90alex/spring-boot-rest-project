-- V2__add_cart_table.sql
-- Cart table: id in binary(16) to store UUIDs
CREATE TABLE cart
(
    id              BINARY(16) DEFAULT (uuid_to_bin(uuid())) NOT NULL,
    date_created    DATE DEFAULT (curdate()) NOT NULL,
    CONSTRAINT      pk_cart PRIMARY KEY (id)
);
