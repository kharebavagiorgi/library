package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Book;
import com.solvd.library.domain.Genre;
import com.solvd.library.persistence.BookRepository;
import com.solvd.library.persistence.GenreRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();
    private final GenreRepository genreRepository = new GenreRepositoryImpl();
    private static final String CREATE_SQL = "INSERT INTO book (isbn, title, publication_date, page_count, genre_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, isbn, title, publication_date, page_count, genre_id FROM book WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE book SET title = ?, isbn = ?, publication_date = ?, page_count = ?, genre_id = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM book WHERE id = ?";

    private static final String COMPLEX_JOIN_SQL =
            "SELECT " +
                    "    b.id AS book_id, b.title, b.isbn, b.page_count, b.publication_date, " +
                    "    g.name AS genre_name, " +
                    "    a.name AS author_name, " +
                    "    r.rating, r.comment " +
                    "FROM book b " +
                    "JOIN genre g ON b.genre_id = g.id " +
                    "LEFT JOIN book_author ba ON b.id = ba.book_id " +
                    "LEFT JOIN author a ON ba.author_id = a.id " +
                    "LEFT JOIN review r ON b.id = r.book_id " +
                    "WHERE b.id = ?";


    @Override
    public void create(Book entity) {
        Connection connection = null;

        // Use Genre Repository to find the DB ID from the Java Enum name
        Long dbGenreId = genreRepository.findIdByName(entity.getGenre().name())
                .orElseThrow(() -> new RuntimeException("Genre not found in database: " + entity.getGenre().name()));

        try {
            connection = POOL.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getIsbn());
                ps.setString(2, entity.getTitle());
                ps.setDate(3, Date.valueOf(entity.getPublicationDate()));
                ps.setInt(4, entity.getPageCount());
                ps.setLong(5, dbGenreId);

                ps.executeUpdate();

                // Retrieve the auto-generated ID
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(1));
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error creating book", e);
            throw new RuntimeException("Database error during book creation.", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        Connection connection = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(extractBook(rs));
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding book by ID: " + id, e);
        } finally {
            POOL.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public void update(Book entity) {
        Connection connection = null;

        // Find the DB ID for the update
        Long dbGenreId = genreRepository.findIdByName(entity.getGenre().name())
                .orElseThrow(() -> new RuntimeException("Genre not found in database: " + entity.getGenre().name()));

        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
                ps.setString(1, entity.getTitle());
                ps.setString(2, entity.getIsbn());
                ps.setDate(3, Date.valueOf(entity.getPublicationDate()));
                ps.setInt(4, entity.getPageCount());
                ps.setLong(5, dbGenreId);
                ps.setLong(6, entity.getId()); // ID is used in the WHERE clause
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error updating book with ID: " + entity.getId(), e);
            throw new RuntimeException("Database error during book update.", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

    @Override
    public void delete(Long id) {
        Connection connection = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error deleting book by ID: " + id, e);
            throw new RuntimeException("Database error during deletion.", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

    private Book extractBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setPageCount(rs.getInt("page_count"));

        // Handle LocalDate mapping
        Date date = rs.getDate("publication_date");
        if (date != null) {
            book.setPublicationDate(date.toLocalDate());
        }

        // Use Genre Repository to map the DB ID (genre_id) back to the Java Enum
        Long genreId = rs.getLong("genre_id");
        genreRepository.findById(genreId).ifPresent(book::setGenre);

        return book;
    }

    private Book extractComplexBook(ResultSet rs) throws SQLException {
        Book book = new Book();

        book.setId(rs.getLong("book_id"));
        book.setTitle(rs.getString("title"));

        book.setGenre(Genre.valueOf(rs.getString("genre_name")));

        return book;
    }

    @Override
    public List<Book> findAll() {
        LOGGER.warn("findAll() method is not yet fully implemented.");
        return new ArrayList<>();
    }

    @Override
    public Book findBookWithAllDetails(Long bookId) {
        Connection connection = null;
        Book book = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(COMPLEX_JOIN_SQL)) {
                ps.setLong(1, bookId);

                try (ResultSet rs = ps.executeQuery()) {
                    List<com.solvd.library.domain.Review> reviews = new ArrayList<>();
                    com.solvd.library.domain.Author author = null;

                    while (rs.next()) {
                        if (book == null) {
                            book = new Book();
                            book.setId(rs.getLong("book_id"));
                            book.setTitle(rs.getString("title"));
                            book.setIsbn(rs.getString("isbn"));
                            book.setPageCount(rs.getInt("page_count"));

                            Date date = rs.getDate("publication_date");
                            if (date != null) book.setPublicationDate(date.toLocalDate());

                            // Genre
                            String genreName = rs.getString("genre_name");
                            if (genreName != null) book.setGenre(Genre.valueOf(genreName));
                        }

                        // Author
                        String authorName = rs.getString("author_name");
                        if (authorName != null && author == null) {
                            author = new com.solvd.library.domain.Author();
                            author.setName(authorName);
                            book.setAuthor(author);
                        }

                        // Review
                        int rating = rs.getInt("rating");
                        if (!rs.wasNull()) {
                            String comment = rs.getString("comment");
                            com.solvd.library.domain.Review review = new com.solvd.library.domain.Review();
                            review.setRating(rating);
                            review.setComment(comment);
                            review.setBook(book);
                            reviews.add(review);
                        }
                    }

                    if (book != null) {
                        book.setReviews(reviews);
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding book with all details for ID: " + bookId, e);
        } finally {
            POOL.releaseConnection(connection);
        }
        return book;
    }

    private static final String INSERT_BOOK_AUTHOR_SQL = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
    public void linkAuthor(Book book) {
        if (book.getAuthor() == null) return;
        Connection connection = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(INSERT_BOOK_AUTHOR_SQL)) {
                ps.setLong(1, book.getId());
                ps.setLong(2, book.getAuthor().getId());
                ps.executeUpdate();
                LOGGER.info("✅ Linked Book ID {} with Author ID {}", book.getId(), book.getAuthor().getId());
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error linking author to book", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

}