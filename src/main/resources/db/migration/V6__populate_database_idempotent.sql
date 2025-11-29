-- V7: Populate database idempotent

-- Categories (idempotent by id)
INSERT INTO categories (id, name)
VALUES
    (1, 'Dairy'),
    (2, 'Bakery'),
    (3, 'Cereals and Rice'),
    (4, 'Eggs'),
    (5, 'Fruits and Vegetables'),
    (6, 'Meat'),
    (7, 'Cheese'),
    (8, 'Drinks'),
    (9, 'Oils and Condiments'),
    (10, 'Sweets and Chocolate')
ON DUPLICATE KEY UPDATE
    name = VALUES(name);

-- Products (idempotent by name)
INSERT INTO products (name, price, `description`, category_id)
VALUES
    ('Whole Milk 1L', 1.29, 'Pasteurized whole milk, 1-liter package. Rich in calcium and vitamins.', 1),
    ('Bread Loaf 400g', 0.95, 'Traditional bread loaf, crunchy crust and soft crumb. Ideal for sandwiches.', 2),
    ('Basmati Rice 1kg', 3.49, 'Long-grain Basmati rice, fragrant aroma, perfect for curries and stews.', 3),
    ('Free-range Eggs 12pcs', 2.75, 'Dozen free-range eggs, size M-L. High-quality protein.', 4),
    ('Gala Apples 1kg', 2.10, 'Gala apples, fresh and sweet, approx. 1 kg pack (4-6 units).', 5),
    ('Chicken Breast 1kg', 6.80, 'Fresh skinless chicken breast, approx. 1 kg tray. Ideal for fillets and stews.', 6),
    ('Manchego Cheese 250g', 5.95, 'Cured Manchego cheese 3-6 months, 250 g piece, intense and slightly spicy flavor.', 7),
    ('Ground Coffee 250g', 4.50, 'Ground coffee 100% Arabica blend, medium roast, 250 g package.', 8),
    ('Olive Oil 500ml', 7.99, 'Extra virgin olive oil, 500 ml, first cold press.', 9),
    ('Dark Chocolate 70g', 1.85, '70% cocoa dark chocolate bar, 70 g. Deep flavor, less sweet.', 10)
ON DUPLICATE KEY UPDATE
                     price = VALUES(price),
                     `description` = VALUES(`description`),
                     category_id = VALUES(category_id);
