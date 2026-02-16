-- Update all products with high-quality image URLs
USE petcare;

-- Food & Treats
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=400' WHERE name = 'Premium Dog Food';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1611003228941-98852ba62227?w=400' WHERE name = 'Cat Food Mix';

-- Toys
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1535930891776-0c2dfb7fda1a?w=400' WHERE name = 'Rubber Chew Toy';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1545249390-6bdfa286032f?w=400' WHERE name = 'Feather Cat Toy';

-- Grooming
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1608848461950-0fe51dfc41cb?w=400' WHERE name = 'Pet Shampoo';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=400' WHERE name = 'Dog Brush';

-- Accessories
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=400' WHERE name = 'Dog Collar';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1583511655826-05700d62f85d?w=400' WHERE name = 'Pet Leash';

-- Health & Wellness
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=400' WHERE name = 'Fish Oil Supplement';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1623387641168-d9803ddd3f35?w=400' WHERE name = 'Vitamin Treats';

-- Verify the updates
SELECT id, name, price, stock_quantity, image_url FROM products ORDER BY category_id, name;
