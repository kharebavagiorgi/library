package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Author;
import com.solvd.library.persistence.AuthorRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorRepositoryImpl implements AuthorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();

    // SQL
    private static final String CREATE_SQL = "INSERT INTO author (name) VALUES (?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name FROM author WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, name FROM author";
    private static final String UPDATE_SQL = "UPDATE author SET name = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM author WHERE id = ?";

    @Override
    public void create(Author entity) {
        Connection connection = null;

        try {
            connection = POOL.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getName());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(1));
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error creating author", e);
            throw new RuntimeException("Database error during author creation.", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Author> findById(Long id) {
        Connection connection = null;

        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(extractAuthor(rs));
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding author by ID: " + id, e);
        } finally {
            POOL.releaseConnection(connection);
        }

        return Optional.empty();
    }

    @Override
    public List<Author> findAll() {
        List<Author> list = new ArrayList<>();
        Connection connection = null;

        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(extractAuthor(rs));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error fetching all authors", e);
        } finally {
            POOL.releaseConnection(connection);
        }

        return list;
    }

    @Override
    public void update(Author entity) {
        Connection connection = null;

        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
                ps.setString(1, entity.getName());
                ps.setLong(2, entity.getId());
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error updating author with ID: " + entity.getId(), e);
            throw new RuntimeException("Database error during author update.", e);
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
            LOGGER.error("Error deleting author with ID: " + id, e);
            throw new RuntimeException("Author deletion error.", e);
        } finally {
            POOL.releaseConnection(connection);
        }
    }

    private Author extractAuthor(ResultSet rs) throws SQLException {
        Author a = new Author();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        return a;
    }
}
