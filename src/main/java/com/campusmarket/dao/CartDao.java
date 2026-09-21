package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.Listing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC data-access for cart_items. The logged-in cart is persisted in the
 * database keyed by the HttpSession id (session.getId()), matching the
 * brief's schema (cart_id, session_id, listing_id, added_at).
 */
public class CartDao {

    public boolean contains(String sessionId, long listingId) {
        String sql = "SELECT 1 FROM cart_items WHERE session_id=? AND listing_id=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, listingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Add a listing to the session cart (ignored if already present). */
    public void add(String sessionId, long listingId) {
        if (contains(sessionId, listingId)) {
            return;
        }
        String sql = "INSERT INTO cart_items(session_id, listing_id) VALUES (?,?)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, listingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String sessionId, long listingId) {
        String sql = "DELETE FROM cart_items WHERE session_id=? AND listing_id=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, listingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear(String sessionId) {
        String sql = "DELETE FROM cart_items WHERE session_id=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** The listings currently in this session's cart that are still AVAILABLE. */
    public List<Listing> items(String sessionId) {
        String sql = "SELECT l.*, s.name AS seller_name "
                + "FROM cart_items ci "
                + "JOIN listings l ON l.listing_id = ci.listing_id "
                + "JOIN students s ON s.student_id = l.seller_id "
                + "WHERE ci.session_id = ? "
                + "ORDER BY ci.added_at";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Listing> out = new ArrayList<>();
                while (rs.next()) {
                    Listing l = new Listing();
                    l.setId(rs.getLong("listing_id"));
                    l.setSellerId(rs.getLong("seller_id"));
                    l.setSellerName(rs.getString("seller_name"));
                    l.setTitle(rs.getString("title"));
                    l.setDescription(rs.getString("description"));
                    l.setCategory(rs.getString("category"));
                    l.setPrice(rs.getBigDecimal("price"));
                    l.setCondition(rs.getString("item_condition"));
                    l.setStatus(rs.getString("status"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(l);
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int count(String sessionId) {
        String sql = "SELECT COUNT(*) FROM cart_items ci JOIN listings l ON l.listing_id = ci.listing_id "
                + "WHERE ci.session_id=? AND l.status='AVAILABLE'";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
