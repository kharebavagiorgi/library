package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Review;
import com.solvd.library.persistence.ReviewRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewRepositoryImpl implements ReviewRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();

    private static final String CREATE_SQL =
            "INSERT INTO review (rating, comment, book_id) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, rating, comment, book_id FROM review WHERE id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT id, rating, comment, book_id FROM review";

    private static final String UPDATE_SQL =
            "UPDATE review SET rating = ?, comment = ?, book_id = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM review WHERE id = ?";

    private static final String DELETE_BY_BOOK_ID_SQL = // <-- NEW SQL CONSTANT
            "DELETE FROM review WHERE book_id = ?";

    @Override
    public void create(Review entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, entity.getRating());
                ps.setString(2, entity.getComment());

                if (entity.getBookId() != null) {
                    ps.setLong(3, entity.getBookId());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) entity.setId(rs.getLong(1));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error creating review", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    @Override
    public Optional<Review> findById(Long id) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
                ps.setLong(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(extractReview(rs));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding review", e);
        } finally {
            POOL.releaseConnection(conn);
        }

        return Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        List<Review> list = new ArrayList<>();
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) list.add(extractReview(rs));
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error fetching reviews", e);
        } finally {
            POOL.releaseConnection(conn);
        }

        return list;
    }

    @Override
    public void update(Review entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setInt(1, entity.getRating());
                ps.setString(2, entity.getComment());

                if (entity.getBookId() != null) {
                    ps.setLong(3, entity.getBookId());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                ps.setLong(4, entity.getId());
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error updating review", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    @Override
    public void delete(Long id) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error deleting review", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    @Override // <-- NEW METHOD IMPLEMENTATION
    public void deleteByBookId(Long bookId) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_BOOK_ID_SQL)) {
                ps.setLong(1, bookId);
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error deleting reviews for book ID: " + bookId, e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    private Review extractReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));

        Long bookId = rs.getLong("book_id");
        if (bookId != 0) {
            r.setBookId(bookId);
        }

        return r;
    }
}