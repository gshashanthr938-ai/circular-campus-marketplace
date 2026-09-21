package com.campusmarket.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Idempotent migrations for databases created by older project versions. */
public final class SchemaMigration {
    private SchemaMigration() {}

    public static void run() throws SQLException {
        try (Connection c = Db.getConnection(); Statement st = c.createStatement()) {
            add(st, "ALTER TABLE students ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'STUDENT'");
            add(st, "ALTER TABLE students ADD COLUMN phone VARCHAR(20)");
            add(st, "ALTER TABLE students ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            add(st, "ALTER TABLE listings ADD COLUMN image_path VARCHAR(255)");
            add(st, "ALTER TABLE listings ADD COLUMN moderation_note VARCHAR(500)");
            add(st, "ALTER TABLE transactions ADD COLUMN payment_method VARCHAR(20) NOT NULL DEFAULT 'UPI'");
            add(st, "ALTER TABLE transactions ADD COLUMN payment_reference VARCHAR(80)");
            add(st, "ALTER TABLE transactions ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED'");
            add(st, "ALTER TABLE transactions ADD COLUMN terms_accepted_at TIMESTAMP");
            st.executeUpdate("UPDATE students SET wallet_balance=0");
            st.executeUpdate("UPDATE students SET phone='+91 90000 10001' WHERE email='asha@campus.edu' AND phone IS NULL");
            st.executeUpdate("UPDATE students SET phone='+91 90000 10002' WHERE email='rahul@campus.edu' AND phone IS NULL");
            st.executeUpdate("UPDATE students SET phone='+91 90000 10003' WHERE email='neha@campus.edu' AND phone IS NULL");
            st.executeUpdate("UPDATE students SET role='ADMIN' WHERE email='asha@campus.edu'");
            st.executeUpdate("UPDATE listings SET image_path='generated/data-structures-textbook.png' WHERE title='Data Structures Textbook (Cormen)' AND image_path IS NULL");
            st.executeUpdate("UPDATE listings SET image_path='generated/student-calculator.png' WHERE title='Scientific Calculator (Casio 991)' AND image_path IS NULL");
            st.executeUpdate("UPDATE listings SET image_path='generated/foldable-study-table.png' WHERE title='Study Table (foldable)' AND image_path IS NULL");
            st.executeUpdate("UPDATE listings SET image_path='generated/adjustable-led-lamp.png' WHERE title='Table Lamp (LED)' AND image_path IS NULL");
            st.executeUpdate("UPDATE listings SET image_path='generated/engineering-drawing-kit.png' WHERE title='Engineering Drawing Kit' AND image_path IS NULL");
            st.executeUpdate("UPDATE listings SET image_path='generated/bluetooth-headphones.png' WHERE title='Bluetooth Headphones' AND image_path IS NULL");
        }
    }

    private static void add(Statement st, String sql) throws SQLException {
        try { st.execute(sql); }
        catch (SQLException e) {
            String state = e.getSQLState();
            if (!("42S21".equals(state) || "42121".equals(state))) throw e;
        }
    }
}
