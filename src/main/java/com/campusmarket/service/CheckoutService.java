package com.campusmarket.service;

import com.campusmarket.db.Db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles checkout as a single atomic database transaction:
 *   - totals the session cart,
 *   - checks the buyer's wallet balance,
 *   - moves money buyer -> seller wallets,
 *   - records a transaction row per item and marks each listing SOLD,
 *   - awards the buyer sustainability points,
 *   - clears the cart.
 * If anything fails, the whole thing rolls back (nothing half-happens).
 */
public class CheckoutService {

    /** +10 sustainability points per item resold (stretch-goal feature). */
    private static final int POINTS_PER_ITEM = 10;

    public static class Result {
        public final boolean success;
        public final String message;
        public final BigDecimal total;
        public final int itemCount;

        Result(boolean success, String message, BigDecimal total, int itemCount) {
            this.success = success;
            this.message = message;
            this.total = total;
            this.itemCount = itemCount;
        }
    }

    private static class CartRow {
        long listingId;
        long sellerId;
        BigDecimal price;
    }

    public Result checkout(long buyerId, String sessionId) {
        Connection c = null;
        try {
            c = Db.getConnection();
            c.setAutoCommit(false); // begin transaction

            // 1. Load cart rows (only available items, and never the buyer's own items).
            List<CartRow> rows = loadCart(c, buyerId, sessionId);
            if (rows.isEmpty()) {
                c.rollback();
                return new Result(false, "Your cart is empty.", BigDecimal.ZERO, 0);
            }

            BigDecimal total = BigDecimal.ZERO;
            for (CartRow r : rows) {
                total = total.add(r.price);
            }

            // 2. Check wallet balance.
            BigDecimal wallet = walletOf(c, buyerId);
            if (wallet.compareTo(total) < 0) {
                c.rollback();
                return new Result(false,
                        "Insufficient wallet balance. Need " + total + ", have " + wallet + ".",
                        total, rows.size());
            }

            // 3. Per item: record transaction, mark listing SOLD, pay the seller.
            for (CartRow r : rows) {
                insertTransaction(c, buyerId, r.listingId, r.price);
                markSold(c, r.listingId);
                creditSeller(c, r.sellerId, r.price);
            }

            // 4. Debit buyer wallet + award sustainability points.
            debitBuyer(c, buyerId, total);
            addPoints(c, buyerId, rows.size() * POINTS_PER_ITEM);

            // 5. Empty the cart.
            clearCart(c, sessionId);

            c.commit();
            return new Result(true,
                    "Purchase successful! " + rows.size() + " item(s) for " + total + ".",
                    total, rows.size());

        } catch (Exception e) {
            rollbackQuietly(c);
            throw new RuntimeException("Checkout failed", e);
        } finally {
            closeQuietly(c);
        }
    }

    private List<CartRow> loadCart(Connection c, long buyerId, String sessionId) throws Exception {
        String sql = "SELECT l.listing_id, l.seller_id, l.price "
                + "FROM cart_items ci JOIN listings l ON l.listing_id = ci.listing_id "
                + "WHERE ci.session_id = ? AND l.status = 'AVAILABLE' AND l.seller_id <> ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartRow> out = new ArrayList<>();
                while (rs.next()) {
                    CartRow r = new CartRow();
                    r.listingId = rs.getLong("listing_id");
                    r.sellerId = rs.getLong("seller_id");
                    r.price = rs.getBigDecimal("price");
                    out.add(r);
                }
                return out;
            }
        }
    }

    private BigDecimal walletOf(Connection c, long studentId) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT wallet_balance FROM students WHERE student_id=?")) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    private void insertTransaction(Connection c, long buyerId, long listingId, BigDecimal amount)
            throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO transactions(buyer_id, listing_id, amount) VALUES (?,?,?)")) {
            ps.setLong(1, buyerId);
            ps.setLong(2, listingId);
            ps.setBigDecimal(3, amount);
            ps.executeUpdate();
        }
    }

    private void markSold(Connection c, long listingId) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE listings SET status='SOLD' WHERE listing_id=?")) {
            ps.setLong(1, listingId);
            ps.executeUpdate();
        }
    }

    private void creditSeller(Connection c, long sellerId, BigDecimal amount) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE students SET wallet_balance = wallet_balance + ? WHERE student_id=?")) {
            ps.setBigDecimal(1, amount);
            ps.setLong(2, sellerId);
            ps.executeUpdate();
        }
    }

    private void debitBuyer(Connection c, long buyerId, BigDecimal amount) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE students SET wallet_balance = wallet_balance - ? WHERE student_id=?")) {
            ps.setBigDecimal(1, amount);
            ps.setLong(2, buyerId);
            ps.executeUpdate();
        }
    }

    private void addPoints(Connection c, long studentId, int points) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE students SET sustainability_points = sustainability_points + ? WHERE student_id=?")) {
            ps.setInt(1, points);
            ps.setLong(2, studentId);
            ps.executeUpdate();
        }
    }

    private void clearCart(Connection c, String sessionId) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "DELETE FROM cart_items WHERE session_id=?")) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        }
    }

    private void rollbackQuietly(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (Exception ignore) {
            }
        }
    }

    private void closeQuietly(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignore) {
            }
        }
    }
}
