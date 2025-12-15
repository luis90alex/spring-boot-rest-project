-- V1__initial_migration_h2.sql

CREATE TABLE addresses (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           street VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           state VARCHAR(255) NOT NULL,
                           zip VARCHAR(255) NOT NULL,
                           user_id BIGINT NOT NULL
);

CREATE TABLE categories (
                            id TINYINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          description CLOB NOT NULL,
                          category_id TINYINT,
                          CONSTRAINT unique_product_name UNIQUE (name)
);

CREATE TABLE profiles (
                          id BIGINT PRIMARY KEY,
                          bio CLOB,
                          phone_number VARCHAR(15),
                          date_of_birth DATE,
                          loyalty_points INT DEFAULT 0
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE wishlist (
                          product_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          PRIMARY KEY (product_id, user_id)
);

-- Foreign keys

ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE wishlist
    ADD CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE;

ALTER TABLE wishlist
    ADD CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_user FOREIGN KEY (id) REFERENCES users (id);