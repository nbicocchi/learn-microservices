/*CREATE EXTENSION IF NOT EXISTS "uuid-ossp";*/

-- Populate the 'reviews' table
INSERT INTO review(review_id, product_id, author, content, subject) VALUES
                                                                        (default, 1, 'Alice', 'Excellent clarity and practical advice.', 'Utility and Clarity'),
                                                                        (default, 2, 'Bob', 'Great patterns explained well.', 'Design and Patterns'),
                                                                        (default, 3, 'Charlie', 'Invaluable insights on pragmatic coding.', 'Best Practices'),
                                                                        (default, 4, 'Diana', 'A must-read for refactoring legacy code.', 'Refactoring'),
                                                                        (default, 5, 'Eve', 'Detailed and comprehensive, very useful.', 'Software Construction'),
                                                                        (default, 6, 'Frank', 'Practical advice for professional coding.', 'Professionalism');

