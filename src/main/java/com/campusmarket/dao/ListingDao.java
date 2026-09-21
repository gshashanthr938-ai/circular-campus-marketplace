package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.Listing;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JDBC data-access for the listings table (the Listings + Browse modules). */
public class ListingDao {

    private static final String BASE_SELECT =
            "SELECT l.*, s.name AS seller_name, "
                    + "COALESCE((SELECT AVG(r.rating) FROM reviews r WHERE r.listing_id=l.listing_id),0) AS avg_rating, "
                    + "(SELECT COUNT(*) FROM reviews r WHERE r.listing_id=l.listing_id) AS review_count, "
                    + "s.created_at AS seller_member_since, "
                    + "(SELECT COUNT(*) FROM transactions tx JOIN listings sold ON sold.listing_id=tx.listing_id WHERE sold.seller_id=l.seller_id AND tx.payment_status='COMPLETED') AS seller_sales_count, "
                    + "COALESCE((SELECT AVG(r2.rating) FROM reviews r2 JOIN listings rated ON rated.listing_id=r2.listing_id WHERE rated.seller_id=l.seller_id),0) AS seller_avg_rating, "
                    + "(SELECT COUNT(*) FROM reviews r3 JOIN listings rated2 ON rated2.listing_id=r3.listing_id WHERE rated2.seller_id=l.seller_id) AS seller_review_count FROM listings l "
                    + "JOIN students s ON s.student_id = l.seller_id ";

    private Listing map(ResultSet rs) throws SQLException {
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
        l.setImagePath(rs.getString("image_path"));
        l.setModerationNote(rs.getString("moderation_note"));
        l.setAverageRating(rs.getDouble("avg_rating"));
        l.setReviewCount(rs.getInt("review_count"));
        l.setSellerMemberSince(rs.getTimestamp("seller_member_since"));
        l.setSellerSalesCount(rs.getInt("seller_sales_count"));
        l.setSellerAverageRating(rs.getDouble("seller_avg_rating"));
        l.setSellerReviewCount(rs.getInt("seller_review_count"));
        return l;
    }

    public Listing findById(long id) {
        String sql = BASE_SELECT + "WHERE l.listing_id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Browse/search available listings with optional filters.
     *
     * @param search   free-text match on title (nullable)
     * @param category exact category (nullable / "" = any)
     * @param maxPrice upper price bound (nullable)
     */
    public List<Listing> search(String search, String category, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE l.status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND LOWER(l.title) LIKE ?");
            params.add("%" + search.toLowerCase() + "%");
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND l.category = ?");
            params.add(category);
        }
        if (maxPrice != null) {
            sql.append(" AND l.price <= ?");
            params.add(maxPrice);
        }
        sql.append(" ORDER BY l.created_at DESC");

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Listing> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Listing> findBySeller(long sellerId) {
        String sql = BASE_SELECT + "WHERE l.seller_id = ? ORDER BY l.created_at DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Listing> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Listing> findAll() {
        String sql = BASE_SELECT + "ORDER BY l.created_at DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Listing> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Distinct categories currently available, for the filter dropdown. */
    public List<String> distinctCategories() {
        String sql = "SELECT DISTINCT category FROM listings ORDER BY category";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> out = new ArrayList<>();
            while (rs.next()) {
                out.add(rs.getString(1));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long create(long sellerId, String title, String description, String category,
                       BigDecimal price, String condition) {
        String sql = "INSERT INTO listings(seller_id, title, description, category, price, "
                + "item_condition, status) VALUES (?,?,?,?,?,?,'AVAILABLE')";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sellerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, category);
            ps.setBigDecimal(5, price);
            ps.setString(6, condition);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(long id, long sellerId, String title, String description,
                       String category, BigDecimal price, String condition) {
        String sql = "UPDATE listings SET title=?, description=?, category=?, price=?, item_condition=? "
                + "WHERE listing_id=? AND seller_id=? AND status='AVAILABLE'";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, category);
            ps.setBigDecimal(4, price);
            ps.setString(5, condition);
            ps.setLong(6, id);
            ps.setLong(7, sellerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Remove a listing (only if it belongs to the seller and is not sold). */
    public void delete(long id, long sellerId) {
        String sql = "UPDATE listings SET status='REMOVED' WHERE listing_id=? AND seller_id=? AND status='AVAILABLE'";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, sellerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Admin moderation: update the safe local image path and listing state. */
    public boolean moderate(long id, String imagePath, String status, String note) {
        String sql = "UPDATE listings SET image_path=?, status=?, moderation_note=? WHERE listing_id=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, imagePath); ps.setString(2, status); ps.setString(3, note); ps.setLong(4, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
