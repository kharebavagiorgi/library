DROP DATABASE IF EXISTS library_management_db;
CREATE DATABASE library_management_db;
USE library_management_db;

CREATE TABLE library (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE genre (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL, -- e.g., FICTION, SCIENCE
    PRIMARY KEY (id)
);

CREATE TABLE department (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE address (
    id BIGINT NOT NULL AUTO_INCREMENT,
    street VARCHAR(255),
    city VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    registration_date_time DATETIME,
    address_id BIGINT UNIQUE, -- One-to-One: A user has one address
    PRIMARY KEY (id),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE book (
    id BIGINT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255),
    publication_date DATE,
    page_count INT,
    -- FK for Genre
    genre_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE review (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rating TINYINT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    -- FK for Book (Many-to-One)
    book_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE employee (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    -- FK for Department (Many-to-One)
    department_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE user_review_contribution (
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, review_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (review_id) REFERENCES review(id)
);

CREATE TABLE author (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE book_author (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (author_id) REFERENCES author(id)
);

CREATE TABLE library_book_asset (
    library_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    asset_status VARCHAR(50), -- e.g., 'IN_STOCK', 'CHECKED_OUT'
    PRIMARY KEY (library_id, book_id),
    FOREIGN KEY (library_id) REFERENCES library(id),
    FOREIGN KEY (book_id) REFERENCES book(id)
);

-- insertions

INSERT INTO genre (id, name) VALUES (1, 'FICTION'), (2, 'SCIENCE'), (3, 'HISTORY');

INSERT INTO address (id, street, city) VALUES 
(1, '101 Main St', 'Metropolis'), 
(2, '50 Oak Ave', 'Gotham');

INSERT INTO user (id, is_active, registration_date_time, address_id) VALUES
(101, TRUE, '2023-01-15 10:30:00', 1),
(102, FALSE, '2024-03-20 14:45:00', 2);

INSERT INTO department (id, name) VALUES
(201, 'Fiction & Literature'),
(202, 'Science & Tech');

INSERT INTO employee (id, first_name, last_name, department_id) VALUES
(301, 'Alice', 'Smith', 201),
(302, 'Bob', 'Jones', 202);

INSERT INTO book (id, isbn, title, publication_date, page_count, genre_id) VALUES
(1, '978-0321765723', 'The Art of Programming', '2011-05-25', 888, 2),
(2, '978-1503734008', 'Historical Contexts', '2018-10-10', 450, 3),
(3, '978-0596009205', 'Head First Java', '2005-02-09', 720, 2);

INSERT INTO review (id, rating, comment, book_id) VALUES
(1, 5, 'Excellent resource for deep learning.', 1),
(2, 4, 'A bit dense, but very comprehensive.', 1);

INSERT INTO author (id, name) VALUES
(10, 'John Smith'),
(11, 'Jane Doe');

INSERT INTO book_author (book_id, author_id) VALUES
(1, 10),
(3, 11);

INSERT INTO user_review_contribution (user_id, review_id) VALUES
(101, 1),
(101, 2);

-- update

UPDATE user SET is_active = TRUE WHERE id = 102;

UPDATE book SET title = 'Head First Java (2nd Edition)' WHERE id = 3;

UPDATE review SET `rating` = 5, comment = 'Excellent and comprehensive.' WHERE id = 2;

UPDATE employee SET last_name = 'Johnson' WHERE id = 301;

UPDATE book SET page_count = 500 WHERE genre_id = 3;

UPDATE address SET street = '202 Central Ave' WHERE id = 1;

UPDATE department SET name = 'Fiction & Classics' WHERE id = 201;

UPDATE employee SET department_id = 202 WHERE id = 301;

UPDATE author SET name = 'J. R. Smith' WHERE id = 10;

UPDATE user SET registration_date_time = '2024-03-21 09:00:00' WHERE id = 102;

-- deletion

DELETE FROM user_review_contribution WHERE user_id = 101;

DELETE FROM review WHERE book_id = 1;

DELETE FROM book_author WHERE book_id = 1;

DELETE FROM book WHERE id = 1;

DELETE FROM book_author WHERE book_id = 3;

DELETE FROM book WHERE id = 3;

DELETE FROM book WHERE id = 2;

DELETE FROM genre WHERE id = 3;

DELETE FROM employee WHERE id = 301;

DELETE FROM department WHERE id = 201;

-- alter

ALTER TABLE book
ADD COLUMN last_borrowed_date DATE AFTER publication_date;

ALTER TABLE user
ADD COLUMN email VARCHAR(255) UNIQUE AFTER is_active;

ALTER TABLE employee
MODIFY COLUMN last_name VARCHAR(150);

ALTER TABLE employee
ADD COLUMN library_id BIGINT,
ADD CONSTRAINT fk_employee_library
    FOREIGN KEY (library_id) REFERENCES library(id);

ALTER TABLE user
RENAME TO library_user;

-- join

SELECT
    l.id AS library_id,
    l.name AS library_name,

    b.id AS book_id,
    b.title AS book_title,
    b.isbn,
    b.publication_date,
    b.page_count,
    g.name AS genre_name,
    lba.asset_status,

    a.id AS author_id,
    a.name AS author_name,
    
    r.id AS review_id,
    r.rating,
    r.comment,

    u.id AS user_id,
    u.is_active,
    u.registration_date_time,
    u.email,
    ad.street AS user_street,
    ad.city AS user_city,

    e.id AS employee_id,
    e.first_name,
    e.last_name,
    d.name AS department_name,
    e.library_id AS employee_library_id

FROM library l
    LEFT JOIN library_book_asset lba ON l.id = lba.library_id

    LEFT JOIN book b ON lba.book_id = b.id

    LEFT JOIN genre g ON b.genre_id = g.id

    LEFT JOIN book_author ba ON b.id = ba.book_id

    LEFT JOIN author a ON ba.author_id = a.id

    LEFT JOIN review r ON b.id = r.book_id

    LEFT JOIN user_review_contribution urc ON r.id = urc.review_id

    LEFT JOIN library_user u ON urc.user_id = u.id  -- use `library_user` (renamed from `user`)

    LEFT JOIN address ad ON u.address_id = ad.id

    LEFT JOIN employee e ON e.library_id = l.id

    LEFT JOIN department d ON e.department_id = d.id

ORDER BY l.id, b.id, r.id, u.id;

-- inner/outter/left/right

SELECT 
    b.id AS book_id,
    b.title,
    g.name AS genre_name
FROM book b
INNER JOIN genre g ON b.genre_id = g.id;

SELECT 
    b.title AS book_title,
    r.id AS review_id,
    r.rating,
    r.comment
FROM book b
LEFT JOIN review r ON b.id = r.book_id;

SELECT 
    e.first_name,
    e.last_name,
    d.name AS department_name
FROM employee e
RIGHT JOIN department d ON e.department_id = d.id;

SELECT 
    u.id AS user_id,
    u.is_active,
    a.street,
    a.city
FROM library_user u
LEFT JOIN address a ON u.address_id = a.id

UNION

SELECT 
    u.id AS user_id,
    u.is_active,
    a.street,
    a.city
FROM library_user u
RIGHT JOIN address a ON u.address_id = a.id;

SELECT 
    b.title AS book_title,
    a.name AS author_name
FROM book b
LEFT JOIN book_author ba ON b.id = ba.book_id
LEFT JOIN author a ON ba.author_id = a.id;

-- without having

USE library_management_db;

SELECT 
    g.name AS genre_name,
    COUNT(b.id) AS total_books
FROM book b
JOIN genre g ON b.genre_id = g.id
GROUP BY g.name;

SELECT 
    b.title AS book_title,
    AVG(r.rating) AS avg_rating
FROM book b
LEFT JOIN review r ON b.id = r.book_id
GROUP BY b.title;

SELECT 
    u.id AS user_id,
    COUNT(urc.review_id) AS total_reviews
FROM library_user u
LEFT JOIN user_review_contribution urc ON u.id = urc.user_id
GROUP BY u.id;

SELECT 
    d.name AS department_name,
    COUNT(e.id) AS total_employees
FROM department d
LEFT JOIN employee e ON d.id = e.department_id
GROUP BY d.name;

SELECT 
    g.name AS genre_name,
    AVG(b.page_count) AS avg_pages
FROM book b
JOIN genre g ON b.genre_id = g.id
GROUP BY g.name;

SELECT 
    b.title AS book_title,
    MAX(r.rating) AS max_rating,
    MIN(r.rating) AS min_rating
FROM book b
LEFT JOIN review r ON b.id = r.book_id
GROUP BY b.title;

SELECT 
    l.name AS library_name,
    COUNT(lba.book_id) AS total_books_available
FROM library l
LEFT JOIN library_book_asset lba ON l.id = lba.library_id
GROUP BY l.name;

-- with having

SELECT 
    g.name AS genre_name,
    COUNT(b.id) AS total_books
FROM book b
JOIN genre g ON b.genre_id = g.id
GROUP BY g.name
HAVING COUNT(b.id) > 1;

SELECT 
    b.title AS book_title,
    AVG(r.rating) AS avg_rating
FROM book b
JOIN review r ON b.id = r.book_id
GROUP BY b.title
HAVING AVG(r.rating) > 4;

SELECT 
    u.id AS user_id,
    COUNT(urc.review_id) AS total_reviews
FROM library_user u
JOIN user_review_contribution urc ON u.id = urc.user_id
GROUP BY u.id
HAVING COUNT(urc.review_id) > 1;

SELECT 
    d.name AS department_name,
    COUNT(e.id) AS total_employees
FROM department d
JOIN employee e ON d.id = e.department_id
GROUP BY d.name
HAVING COUNT(e.id) > 1;

SELECT 
    g.name AS genre_name,
    AVG(b.page_count) AS avg_pages
FROM book b
JOIN genre g ON b.genre_id = g.id
GROUP BY g.name
HAVING AVG(b.page_count) > 600;

SELECT 
    b.title AS book_title,
    COUNT(r.id) AS total_reviews
FROM book b
JOIN review r ON b.id = r.book_id
GROUP BY b.title
HAVING COUNT(r.id) >= 2;

SELECT 
    l.name AS library_name,
    COUNT(lba.book_id) AS total_books_held
FROM library l
JOIN library_book_asset lba ON l.id = lba.library_id
GROUP BY l.name
HAVING COUNT(lba.book_id) > 1;
