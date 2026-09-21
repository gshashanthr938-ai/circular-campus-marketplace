package com.campusmarket.db;

import com.campusmarket.util.PasswordUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Runs once when the web application starts up:
 *  1. Creates the tables from schema.sql (if they don't exist yet).
 *  2. Seeds a few demo students and listings (only if the DB is empty),
 *     so the app is immediately demonstrable.
 */
@WebListener
public class DbBootstrap implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            createSchema();
            SchemaMigration.run();
            seedIfEmpty();
            SchemaMigration.run();
            Catalog.install();
            Catalog.applyMissingImages();
            System.out.println("[DbBootstrap] Database ready.");
        } catch (Exception e) {
            throw new RuntimeException("Database initialisation failed", e);
        }
    }

    private void createSchema() throws Exception {
        String sql = readResource("schema.sql");
        try (Connection c = Db.getConnection(); Statement st = c.createStatement()) {
            for (String stmt : sql.split(";")) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
        }
    }

    private void seedIfEmpty() throws Exception {
        try (Connection c = Db.getConnection()) {
            if (countStudents(c) > 0) {
                return; // already seeded
            }
            System.out.println("[DbBootstrap] Seeding demo data...");

            long asha  = insertStudent(c, "Asha Menon",    "asha@campus.edu",  "+91 90000 10001", "password");
            long rahul = insertStudent(c, "Rahul Verma",   "rahul@campus.edu", "+91 90000 10002", "password");
            long neha  = insertStudent(c, "Neha Gupta",    "neha@campus.edu",  "+91 90000 10003", "password");

            insertListing(c, asha,  "Data Structures Textbook (Cormen)", "Barely used, no markings.",
                    "Books", 450.00, "Like New");
            insertListing(c, asha,  "Scientific Calculator (Casio 991)", "Fully working, exam allowed.",
                    "Electronics", 700.00, "Good");
            insertListing(c, rahul, "Study Table (foldable)", "Great for hostel rooms.",
                    "Furniture", 1200.00, "Good");
            insertListing(c, rahul, "Table Lamp (LED)", "Warm light, adjustable neck.",
                    "Hostel Essentials", 300.00, "Like New");
            insertListing(c, neha,  "Engineering Drawing Kit", "Complete set with compass box.",
                    "Books", 250.00, "Fair");
            insertListing(c, neha,  "Bluetooth Headphones", "Good bass, 8hr battery.",
                    "Electronics", 900.00, "Good");
        }
    }

    private int countStudents(Connection c) throws Exception {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private long insertStudent(Connection c, String name, String email, String phone,
                               String plainPassword) throws Exception {
        String sql = "INSERT INTO students(name, email, phone, password, wallet_balance, sustainability_points) "
                + "VALUES (?,?,?,?,?,0)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, PasswordUtil.hash(plainPassword));
            ps.setBigDecimal(5, java.math.BigDecimal.ZERO);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void insertListing(Connection c, long sellerId, String title, String description,
                               String category, double price, String condition) throws Exception {
        String sql = "INSERT INTO listings(seller_id, title, description, category, price, "
                + "item_condition, status) VALUES (?,?,?,?,?,?,'AVAILABLE')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, category);
            ps.setDouble(5, price);
            ps.setString(6, condition);
            ps.executeUpdate();
        }
    }

    private String readResource(String name) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: " + name);
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            return sb.toString();
        }
    }
}
