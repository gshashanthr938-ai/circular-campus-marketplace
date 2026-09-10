package com.campusmarket.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central JDBC helper. Loads connection settings from {@code db.properties}
 * on the classpath and hands out {@link Connection} objects.
 *
 * <p>Uses plain JDBC (DriverManager) so the JDBC learning objective is
 * demonstrated explicitly, and so the same code works on both H2 and MySQL.</p>
 */
public final class Db {

    private static String url;
    private static String user;
    private static String password;
    private static boolean loaded = false;

    private Db() {
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        Properties p = new Properties();
        try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on the classpath");
            }
            p.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load db.properties", e);
        }

        String driver = p.getProperty("db.driver", "").trim();
        try {
            Class.forName(driver); // register the JDBC driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver not on classpath: " + driver, e);
        }

        url = p.getProperty("db.url", "").trim();
        user = p.getProperty("db.user", "").trim();
        password = p.getProperty("db.password", "").trim();
        loaded = true;
    }

    /** @return a fresh JDBC connection to the configured database. */
    public static Connection getConnection() throws SQLException {
        if (!loaded) {
            load();
        }
        return DriverManager.getConnection(url, user, password);
    }
}
