-- V8__reset_identities_h2.sql
-- reset AUTO_INCREMENT/IDENTITY para que el next value > max(id) insertado manualmente
ALTER TABLE categories ALTER COLUMN id RESTART WITH 11;
ALTER TABLE products  ALTER COLUMN id RESTART WITH 20;
ALTER TABLE users     ALTER COLUMN id RESTART WITH 10;
ALTER TABLE orders    ALTER COLUMN id RESTART WITH 10;
ALTER TABLE profiles  ALTER COLUMN id RESTART WITH 10;
