package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Genre;
import com.solvd.library.persistence.GenreRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class GenreRepositoryImpl implements GenreRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenreRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();

    private static final String FIND_ID_BY_NAME_SQL = "SELECT id FROM genre WHERE name = ?";
    private static final String FIND_NAME_BY_ID_SQL = "SELECT name FROM genre WHERE id = ?";

    @Override
    public Optional<Long> findIdByName(String genreName) {
        Connection connection = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(FIND_ID_BY_NAME_SQL)) {
                ps.setString(1, genreName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getLong("id"));
                    }
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding genre ID by name: " + genreName, e);
        } finally {
            POOL.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        Connection connection = null;
        try {
            connection = POOL.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(FIND_NAME_BY_ID_SQL)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(Genre.valueOf(rs.getString("name")));
                    }
                }
            }
        } catch (InterruptedException | SQLException | IllegalArgumentException e) {
            LOGGER.error("Error finding genre by ID: " + id, e);
        } finally {
            POOL.releaseConnection(connection);
        }
        return Optional.empty();
    }
}