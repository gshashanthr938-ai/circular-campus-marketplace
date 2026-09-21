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

/** JDBC data-access for student accounts. */
public class StudentDao {

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getLong("student_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setWalletBalance(rs.getBigDecimal("wallet_balance"));
        s.setSustainabilityPoints(rs.getInt("sustainability_points"));
        s.setRole(rs.getString("role"));
        return s;
    }

    /** Verify email + password. Returns the Student on success, else null. */
    public Student authenticate(String email, String plainPassword) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.matches(plainPassword, rs.getString("password"))) {
                    Student student = map(rs);
                    if (!rs.getString("password").startsWith("pbkdf2$")) {
                        try (PreparedStatement upgrade=c.prepareStatement("UPDATE students SET password=? WHERE student_id=?")) {
                            upgrade.setString(1,PasswordUtil.hash(plainPassword)); upgrade.setLong(2,student.getId()); upgrade.executeUpdate();
                        }
                    }
                    return student;
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

    /** Create a new student without issuing marketplace money. Returns the new id. */
    public long create(String name, String email, String phone, String plainPassword) {
        String sql = "INSERT INTO students(name,email,phone,password,wallet_balance,sustainability_points) VALUES (?,?,?,?,0,0)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, PasswordUtil.hash(plainPassword));
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
