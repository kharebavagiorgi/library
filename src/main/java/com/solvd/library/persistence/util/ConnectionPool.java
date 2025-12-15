package com.solvd.library.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {

    private static final int MAX_POOL_SIZE = 5;
    private static final String DB_URL = DBConfig.getUrl();
    private static final String USER = DBConfig.getUser();
    private static final String PASS = DBConfig.getPassword();
    private static final String DRIVER = DBConfig.getDriverClassName();

    private static BlockingQueue<Connection> availableConnections;
    private static BlockingQueue<Connection> usedConnections;
    private static volatile ConnectionPool instance;
    private static final ReentrantLock lock = new ReentrantLock();

    private ConnectionPool() {
        if (instance != null) {
            throw new IllegalStateException("ConnectionPool is a Singleton. Use getInstance().");
        }
        availableConnections = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        usedConnections = new ArrayBlockingQueue<>(MAX_POOL_SIZE);

        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            try {
                Class.forName(DRIVER);
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                availableConnections.put(connection);
            } catch (ClassNotFoundException | SQLException | InterruptedException e) {
                throw new RuntimeException("Error initializing Connection Pool.", e);
            }
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Connection getConnection() throws InterruptedException {
        Connection connection = availableConnections.take();
        usedConnections.put(connection);
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            availableConnections.offer(connection);
        }
    }
}