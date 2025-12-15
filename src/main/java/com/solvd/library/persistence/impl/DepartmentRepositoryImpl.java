package com.solvd.library.persistence.impl;

import com.solvd.library.domain.Department;
import com.solvd.library.persistence.DepartmentRepository;
import com.solvd.library.persistence.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentRepositoryImpl implements DepartmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentRepositoryImpl.class);
    private static final ConnectionPool POOL = ConnectionPool.getInstance();

    private static final String CREATE_SQL = "INSERT INTO department (name) VALUES (?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name FROM department WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, name FROM department";
    private static final String UPDATE_SQL = "UPDATE department SET name = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM department WHERE id = ?";

    @Override
    public void create(Department entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getName());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) entity.setId(rs.getLong(1));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Department creation error", e);
            throw new RuntimeException(e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    @Override
    public Optional<Department> findById(Long id) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(extractDepartment(rs));
                }
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error finding department ID " + id, e);
        } finally {
            POOL.releaseConnection(conn);
        }

        return Optional.empty();
    }

    @Override
    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) list.add(extractDepartment(rs));
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error retrieving all departments", e);
        } finally {
            POOL.releaseConnection(conn);
        }

        return list;
    }

    @Override
    public void update(Department entity) {
        Connection conn = null;

        try {
            conn = POOL.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setString(1, entity.getName());
                ps.setLong(2, entity.getId());
                ps.executeUpdate();
            }
        } catch (InterruptedException | SQLException e) {
            LOGGER.error("Error updating department", e);
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
            LOGGER.error("Error deleting department", e);
        } finally {
            POOL.releaseConnection(conn);
        }
    }

    private Department extractDepartment(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        return d;
    }
}
