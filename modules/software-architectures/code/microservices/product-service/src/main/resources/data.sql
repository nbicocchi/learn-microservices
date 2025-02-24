/*CREATE EXTENSION IF NOT EXISTS "uuid-ossp";*/

-- Populate the 'products' table
INSERT INTO product(product_id, name, weight) VALUES
    (default, 'Clean Code', 60),
    (default, 'Design Patterns', 80),
    (default, 'The Pragmatic Programmer', 70),
    (default, 'Refactoring', 65),
    (default, 'Code Complete', 75),
    (default, 'The Clean Coder', 55);