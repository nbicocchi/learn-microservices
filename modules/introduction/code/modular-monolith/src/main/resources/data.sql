/*CREATE EXTENSION IF NOT EXISTS "uuid-ossp";*/

-- Populate the 'products' table
INSERT INTO product(product_id, name, weight) VALUES
                                                  (default, 'Clean Code', 60),
                                                  (default, 'Design Patterns', 80),
                                                  (default, 'The Pragmatic Programmer', 70),
                                                  (default, 'Refactoring', 65),
                                                  (default, 'Code Complete', 75),
                                                  (default, 'The Clean Coder', 55);

-- Populate the 'reviews' table
INSERT INTO review (review_id, product_id, author, content, subject) VALUES
                                                              (default, 1, 'Alice', 'Excellent clarity and practical advice.', 'Utility and Clarity'),
                                                              (default, 2, 'Bob', 'Great patterns explained well.', 'Design and Patterns'),
                                                              (default, 3, 'Charlie', 'Invaluable insights on pragmatic coding.', 'Best Practices'),
                                                              (default, 4, 'Diana', 'A must-read for refactoring legacy code.', 'Refactoring'),
                                                              (default, 5, 'Eve', 'Detailed and comprehensive, very useful.', 'Software Construction'),
                                                              (default, 6, 'Frank', 'Practical advice for professional coding.', 'Professionalism');

-- Populate the 'recommendations' table
INSERT INTO recommendation(recommendation_id, product_id, author, rating, content) VALUES
  (default, 1, 'John', 5, 'If you liked Clean Code, you might also enjoy Code Complete.'),
  (default, 1, 'Jane', 4, 'Consider reading The Clean Coder for more insights.'),
  (default, 2, 'Mike', 5, 'Design Patterns is a great complement to Clean Code.'),
  (default, 2, 'Sara', 4, 'Check out The Pragmatic Programmer for additional design tips.'),
  (default, 3, 'Tom', 5, 'The Pragmatic Programmer offers invaluable insights.'),
  (default, 3, 'Lucy', 4, 'Also, look into Refactoring for legacy code improvement.'),
  (default, 4, 'Harry', 5, 'Refactoring is essential for maintaining code quality.'),
  (default, 4, 'Lily', 4, 'The Clean Coder provides additional professional advice.'),
  (default, 5, 'Oscar', 5, 'Code Complete is comprehensive and detailed.'),
  (default, 5, 'Mia', 4, 'Try reading Clean Code for more practical advice.'),
  (default, 6, 'Sam', 5, 'The Clean Coder is perfect for professional development.'),
  (default, 6, 'Emma', 4, 'Combine with The Pragmatic Programmer for best practices.');
