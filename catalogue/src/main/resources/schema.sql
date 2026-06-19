-- CREATE DATABASE catalogue_db;
-- 🧹 Limpieza absoluta para iniciar desde cero:
DROP TABLE IF EXISTS book CASCADE;

CREATE TABLE book (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      publication_date DATE,
                      category VARCHAR(100),
                      isbn VARCHAR(30) UNIQUE NOT NULL,
                      rating INT CHECK (rating BETWEEN 1 AND 5),
                      visible BOOLEAN DEFAULT TRUE,
                      stock INT DEFAULT 0 CHECK (stock >= 0),
                      price NUMERIC(10,2) CHECK (price >= 0),
                      description TEXT,
                      image VARCHAR(500),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ÍNDICES
CREATE INDEX idx_title ON book(title);
CREATE INDEX idx_author ON book(author);
CREATE INDEX idx_category ON book(category);
CREATE INDEX idx_isbn ON book(isbn);
CREATE INDEX idx_rating ON book(rating);
CREATE INDEX idx_visible ON book(visible);
CREATE INDEX idx_pub_date ON book(publication_date);