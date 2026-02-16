-- Seed product categories (check if exists first)
INSERT INTO categories (name, description) VALUES
('Food & Treats', 'Pet food and treats'),
('Toys', 'Toys and games for pets'),
('Grooming', 'Grooming and hygiene products'),
('Accessories', 'Collars, leashes, and other accessories'),
('Health & Wellness', 'Vitamins, supplements, and health products')
ON DUPLICATE KEY UPDATE name=name;

-- Seed products
INSERT INTO products (category_id, name, description, price, stock_quantity, image_url, created_at, updated_at) VALUES
(1, 'Premium Dog Food', 'High-quality dry dog food with balanced nutrition', 1299.00, 50, NULL, NOW(), NOW()),
(1, 'Cat Food Mix', 'Complete nutrition cat food with seafood', 899.00, 40, NULL, NOW(), NOW()),
(2, 'Rubber Chew Toy', 'Durable rubber toy for dogs', 299.00, 100, NULL, NOW(), NOW()),
(2, 'Feather Cat Toy', 'Interactive feather toy for cats', 199.00, 80, NULL, NOW(), NOW()),
(3, 'Pet Shampoo', 'Gentle shampoo for all pet types', 499.00, 60, NULL, NOW(), NOW()),
(3, 'Dog Brush', 'Professional quality pet brush', 399.00, 45, NULL, NOW(), NOW()),
(4, 'Dog Collar', 'Adjustable nylon dog collar', 349.00, 70, NULL, NOW(), NOW()),
(4, 'Pet Leash', 'Durable 6-foot pet leash', 399.00, 55, NULL, NOW(), NOW()),
(5, 'Fish Oil Supplement', 'Omega-3 fish oil for pet health', 599.00, 30, NULL, NOW(), NOW()),
(5, 'Vitamin Treats', 'Daily vitamin and mineral treats for dogs', 799.00, 40, NULL, NOW(), NOW());
