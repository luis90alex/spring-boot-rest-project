INSERT INTO categories (id, name)
VALUES (1, 'Lácteos'),
       (2, 'Panadería'),
       (3, 'Cereales y arroces'),
       (4, 'Huevos'),
       (5, 'Frutas y Verduras'),
       (6, 'Carnicería'),
       (7, 'Quesos'),
       (8, 'Bebidas'),
       (9, 'Aceites y condimentos'),
       (10, 'Dulces y chocolate');


INSERT INTO products (name, price, `description`, category_id)
VALUES ('Leche entera 1L', 1.29, 'Leche entera pasteurizada, envase 1 litro. Rica en calcio y vitaminas.', 1),
       ('Barra de pan 400g', 0.95,'Barra de pan tradicional, corteza crujiente y miga esponjosa. Ideal para bocadillos.', 2),
       ('Arroz Basmati 1kg', 3.49,'Arroz Basmati de grano largo, aroma perfumado, perfecto para acompañar currys y guisos.', 3),
       ('Huevos camperos 12u', 2.75, 'Docena de huevos de gallinas camperas, tamaño M-L. Alta calidad proteica.', 4),
       ('Manzana Gala 1kg', 2.10, 'Manzana Gala, fruto fresco y dulce, pack aproximado 1 kg (4-6 unidades).', 5),
       ('Pechuga de pollo 1kg', 6.80,'Pechuga de pollo fresca, sin piel, bandeja 1 kg aprox. Ideal para filetes y guisos.', 6),
       ('Queso Manchego 250g', 5.95,'Queso manchego curado 3-6 meses, pieza 250 g, sabor intenso y ligeramente picante.', 7),
       ('Café molido 250g', 4.50, 'Café molido mezcla 100% arábica, tueste medio, paquete 250 g.', 8),
       ('Aceite oliva 500ml', 7.99, 'Aceite de oliva virgen extra, 500 ml, primera extracción en frío.', 9),
       ('Chocolate negro 70g', 1.85, 'Tableta de chocolate negro 70% cacao, 70 g. Sabor profundo y menos dulce.', 10);
