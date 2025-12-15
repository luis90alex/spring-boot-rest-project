-- V4__add_role_to_users_h2.sql
ALTER TABLE users
    ADD role VARCHAR(20) DEFAULT 'USER' NOT NULL;