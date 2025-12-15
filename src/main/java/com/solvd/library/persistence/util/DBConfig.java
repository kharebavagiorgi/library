package com.solvd.library.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static String getUrl() {
        return PROPERTIES.getProperty("db.url");
    }

    public static String getUser() {
        return PROPERTIES.getProperty("db.username");
    }

    public static String getPassword() {
        return PROPERTIES.getProperty("db.password");
    }

    public static String getDriverClassName() {
        return PROPERTIES.getProperty("db.driver");
    }
}