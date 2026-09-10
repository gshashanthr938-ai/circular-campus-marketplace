package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.Student;
import com.campusmarket.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** JDBC data-access for the students table (users + wallet). */
public class StudentDao {

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getLong("student_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setWalletBalance(rs.getBigDecimal("wallet_balance"));
        s.setSustainabilityPoints(rs.getInt("sustainability_points"));
        return s;
    }

    /** Verify email + password. Returns the Student on success, else null. */
    public Student authenticate(String email, String plainPassword) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.matches(plainPassword, rs.getString("password"))) {
                    return map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Student findById(long id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM students WHERE email = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Create a new student with a starting wallet balance. Returns the new id. */
    public long create(String name, String email, String plainPassword, BigDecimal startingBalance) {
        String sql = "INSERT INTO students(name, email, password, wallet_balance, sustainability_points) "
                + "VALUES (?,?,?,?,0)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(plainPassword));
            ps.setBigDecimal(4, startingBalance);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
