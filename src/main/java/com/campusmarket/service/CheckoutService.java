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
 *   - validates a UPI or net-banking payment choice,
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

    public Result checkout(long buyerId, String sessionId, String paymentMethod, String paymentDetail,
                           boolean termsAccepted) {
        if (!termsAccepted) return new Result(false,
                "Please read and accept the purchase terms to continue.", BigDecimal.ZERO, 0);
        String paymentError=validatePayment(paymentMethod,paymentDetail);
        if(paymentError!=null)return new Result(false,paymentError,BigDecimal.ZERO,0);
        String paymentReference="CM-"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase();
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

            // 2. Per item: claim stock and record the confirmed demo payment.
            for (CartRow r : rows) {
                markSold(c, r);
                insertTransaction(c, buyerId, r.listingId, r.price,paymentMethod,paymentReference);
            }

            // 3. Award sustainability points.
            addPoints(c, buyerId, rows.size() * POINTS_PER_ITEM);

            // 4. Empty the cart.
            clearCart(c, sessionId);

            c.commit();
            return new Result(true,
                    "Payment confirmed by " + displayMethod(paymentMethod) + ". Reference " + paymentReference + ".",
                    total, rows.size());

        } catch (IllegalStateException e) {
            rollbackQuietly(c);
            new com.campusmarket.dao.WaitlistDao().joinUnavailableItems(buyerId, sessionId);
            return new Result(false,e.getMessage(),BigDecimal.ZERO,0);
        } catch (java.sql.SQLException e) {
            rollbackQuietly(c);
            if ("40001".equals(e.getSQLState())) {
                new com.campusmarket.dao.WaitlistDao().joinUnavailableItems(buyerId, sessionId);
                return new Result(false,"Another student completed checkout first. You were added to the waitlist.",BigDecimal.ZERO,0);
            }
            throw new RuntimeException("Checkout failed",e);
        } catch (Exception e) {
            rollbackQuietly(c);
            throw new RuntimeException("Checkout failed", e);
        } finally {
            closeQuietly(c);
        }
    }

    private List<CartRow> loadCart(Connection c, long buyerId, String sessionId) throws Exception {
        String sql = "SELECT l.listing_id, l.seller_id, l.price, l.status "
                + "FROM cart_items ci JOIN listings l ON l.listing_id = ci.listing_id "
                + "WHERE ci.session_id = ? ORDER BY l.listing_id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                List<CartRow> out = new ArrayList<>();
                java.util.Set<Long> seen=new java.util.HashSet<>();
                while (rs.next()) {
                    CartRow r = new CartRow();
                    r.listingId = rs.getLong("listing_id");
                    r.sellerId = rs.getLong("seller_id");
                    r.price = rs.getBigDecimal("price");
                    if (!"AVAILABLE".equals(rs.getString("status")) || r.sellerId==buyerId) throw new IllegalStateException("An item is unavailable or belongs to you. Please remove it from the cart.");
                    if(seen.add(r.listingId)) out.add(r);
                }
                return out;
            }
        }
    }

    private void insertTransaction(Connection c, long buyerId, long listingId, BigDecimal amount,
                                   String paymentMethod,String paymentReference)
            throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO transactions(buyer_id,listing_id,amount,payment_method,payment_reference,payment_status,terms_accepted_at) VALUES (?,?,?,?,?,'COMPLETED',CURRENT_TIMESTAMP)")) {
            ps.setLong(1, buyerId);
            ps.setLong(2, listingId);
            ps.setBigDecimal(3, amount);
            ps.setString(4,paymentMethod);
            ps.setString(5,paymentReference);
            ps.executeUpdate();
        }
    }

    private void markSold(Connection c, CartRow row) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE listings SET status='SOLD' WHERE listing_id=? AND status='AVAILABLE' AND price=? AND seller_id=?")) {
            ps.setLong(1, row.listingId);
            ps.setBigDecimal(2,row.price);
            ps.setLong(3,row.sellerId);
            if(ps.executeUpdate()!=1) throw new IllegalStateException("An item was sold or changed. Please review your cart.");
        }
    }

    private String validatePayment(String method,String detail) {
        if("UPI".equals(method)) {
            return detail!=null&&detail.matches("[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}")?null:"Enter a valid UPI ID, for example name@bank.";
        }
        if("NET_BANKING".equals(method)) {
            return java.util.Set.of("SBI","HDFC","ICICI","AXIS","KOTAK","OTHER").contains(detail)?null:"Select a bank for net banking.";
        }
        return "Choose UPI or net banking to continue.";
    }

    private String displayMethod(String method){return "UPI".equals(method)?"UPI":"net banking";}

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
