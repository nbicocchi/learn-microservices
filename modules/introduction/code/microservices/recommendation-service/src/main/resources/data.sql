/*CREATE EXTENSION IF NOT EXISTS "uuid-ossp";*/

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
