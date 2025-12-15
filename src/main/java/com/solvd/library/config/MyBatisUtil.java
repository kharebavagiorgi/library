package com.solvd.library.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.IOException;

public final class MyBatisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisUtil.class);
    private static final SqlSessionFactory sqlSessionFactory;
    private static final String RESOURCE = "mybatis-config.xml";

    static {
        try (InputStream inputStream = Resources.getResourceAsStream(RESOURCE)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            LOGGER.info("SINGLETON: SqlSessionFactory initialized successfully.");
        } catch (IOException e) { // Catch specific IOException
            LOGGER.error("SINGLETON ERROR: Failed to initialize MyBatis from resource: " + RESOURCE, e);
            throw new RuntimeException("Failed to initialize MyBatis", e);
        }
    }

    private MyBatisUtil() {
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public static SqlSession openSession() {
        return sqlSessionFactory.openSession(true);
    }

    public static <T> T getMapper(Class<T> mapperClass) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.getMapper(mapperClass);
        }
    }
}