INSERT INTO orders (user_id, user_email, book_id, book_title, quantity, total, status, created_at)
VALUES
    ('user123', 'comprador1@correo.com', 3, 'Emma', 1, 15.99, 'CREATED', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('user123', 'comprador1@correo.com', 5, 'Jane Eyre', 2, 32.50, 'CREATED', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('user999', 'comprador2@correo.com', 10, 'Adios a las armas', 1, 12.00, 'CREATED', CURRENT_TIMESTAMP - INTERVAL '3 hours');