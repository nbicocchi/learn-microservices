/*CREATE EXTENSION IF NOT EXISTS "uuid-ossp";*/

INSERT INTO product(product_id, name, weight) VALUES (default, 'Product 1', 10);
INSERT INTO product(product_id, name, weight) VALUES (default, 'Product 2', 11);
INSERT INTO product(product_id, name, weight) VALUES (default, 'Product 3', 12);

INSERT INTO recommendation(recommendation_id, author, rating, content, product_id) VALUES (default, 'Michael Jackson', 98, 'huh', 1);

INSERT INTO review(review_id, product_id, author, content, subject) VALUES (default, 1, 'Antonio', 'Ciao', 'simple review');
INSERT INTO review(review_id, product_id, author, content, subject) VALUES (default, 2, 'Antonio', 'Ciao', 'simple review');
INSERT INTO review(review_id, product_id, author, content, subject) VALUES (default, 3, 'Antonio', 'Ciao', 'simple review');
