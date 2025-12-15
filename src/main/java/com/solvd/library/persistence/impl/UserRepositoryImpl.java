package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Address;
import com.solvd.library.domain.User;
import com.solvd.library.persistence.UserRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();


    private static final String CREATE_SQL =
            "INSERT INTO user (is_active, registration_date_time, address_id) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, is_active, registration_date_time, address_id FROM user WHERE id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT id, is_active, registration_date_time, address_id FROM user";

    private static final String UPDATE_SQL =
            "UPDATE user SET is_active = ?, registration_date_time = ?, address_id = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM user WHERE id = ?";

    @Override
    public void create(User entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBoolean(1, entity.isActive());
                ps.setTimestamp(2, Timestamp.valueOf(entity.getRegistrationDateTime()));

                // Assuming address is optional and addressId is primitive Long
                if (entity.getAddress() != null) {
                    ps.setLong(3, entity.getAddress().getId());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) entity.setId(rs.getLong(1));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error creating user", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
                ps.setLong(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(extractUser(rs));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("User lookup failed", e); // Original error logged here
        } finally {
            POOL.releaseConnection(conn);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) list.add(extractUser(rs));
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error fetching users", e);
        } finally {
            POOL.releaseConnection(conn);
        }

        return list;
    }

    @Override
    public void update(User entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setBoolean(1, entity.isActive());
                ps.setTimestamp(2, Timestamp.valueOf(entity.getRegistrationDateTime()));

                if (entity.getAddress() != null) {
                    ps.setLong(3, entity.getAddress().getId());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                ps.setLong(4, entity.getId());
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error updating user", e);
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
            LOGGER.error("Error deleting user", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setActive(rs.getBoolean("is_active"));

        Timestamp ts = rs.getTimestamp("registration_date_time");
        if (ts != null) {
            u.setRegistrationDateTime(ts.toLocalDateTime());
        }

        // Assuming Address is loaded separately/lazily, we only set the ID here
        Long addressId = rs.getLong("address_id");
        if (!rs.wasNull()) {
            Address address = new Address();
            address.setId(addressId);
            u.setAddress(address);
        }

        return u;
    }
}