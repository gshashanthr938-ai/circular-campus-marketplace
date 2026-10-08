package com.campusmarket.service;

import com.campusmarket.db.Db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

/** Cancels an uncollected order and routes its refund atomically. */
public class RefundService {
    private static final Set<String> METHODS = Set.of("ORIGINAL_METHOD", "WALLET");

    public static class Result {
        public final boolean success;
        public final String message;
        Result(boolean success, String message) { this.success = success; this.message = message; }
    }

    private static class Order {
        long listingId;
        long sellerId;
        BigDecimal amount;
        String title;
        String paymentMethod;
        String paymentStatus;
        String fulfillmentStatus;
    }

    public Result refund(long buyerId, long transactionId, String method, String reason) {
        if (!METHODS.contains(method)) return fail("Choose where you want the refund to go.");
        String cleanReason = reason == null ? "" : reason.trim();
        if (cleanReason.length() < 5 || cleanReason.length() > 500)
            return fail("Give a short refund reason (5 to 500 characters).");

        Connection c = null;
        try {
            c = Db.getConnection();
            c.setAutoCommit(false);
            Order order = lockOrder(c, buyerId, transactionId);
            if (order == null) { c.rollback(); return fail("This order was not found."); }
            if (!"COMPLETED".equals(order.paymentStatus)) { c.rollback(); return fail("This payment has already been refunded."); }
            if (!"AWAITING_PICKUP".equals(order.fulfillmentStatus)) { c.rollback(); return fail("A completed pickup cannot be refunded automatically."); }

            String reference = "RF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            if (!markRefunded(c, buyerId, transactionId, method, reference, cleanReason)) {
                c.rollback();
                return fail("The order changed while the refund was being processed. Please refresh.");
            }
            if (!restoreListing(c, order.listingId)) throw new SQLException("Sold listing could not be restored");
            removePoints(c, buyerId);

            if ("WALLET".equals(method)) {
                creditWallet(c, buyerId, transactionId, order.amount, reference, order.title);
            }
            notify(c, buyerId,
                    "Refund approved for “" + order.title + "”. "
                            + ("WALLET".equals(method) ? "The money is now in your CampusMarket wallet." : "The refund was sent to your original " + displayPayment(order.paymentMethod) + " source."),
                    "WALLET".equals(method) ? "/wallet" : "/history");
            notify(c, order.sellerId, "Order #" + transactionId + " for “" + order.title + "” was cancelled and the listing is available again.", "/history");
            notifyWaitlist(c, order.listingId, order.title);
            c.commit();

            return new Result(true, "WALLET".equals(method)
                    ? "Refund complete. ₹" + order.amount.toPlainString() + " was added to your wallet."
                    : "Refund initiated to the original " + displayPayment(order.paymentMethod) + " source. Reference " + reference + ".");
        } catch (SQLException e) {
            rollback(c);
            throw new RuntimeException("Refund failed", e);
        } finally {
            close(c);
        }
    }

    private Order lockOrder(Connection c, long buyerId, long transactionId) throws SQLException {
        String sql = "SELECT t.listing_id,t.amount,t.payment_method,t.payment_status,t.fulfillment_status,"
                + "l.seller_id,l.title FROM transactions t JOIN listings l ON l.listing_id=t.listing_id "
                + "WHERE t.txn_id=? AND t.buyer_id=? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, transactionId); ps.setLong(2, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Order o = new Order();
                o.listingId = rs.getLong("listing_id"); o.sellerId = rs.getLong("seller_id");
                o.amount = rs.getBigDecimal("amount"); o.title = rs.getString("title");
                o.paymentMethod = rs.getString("payment_method"); o.paymentStatus = rs.getString("payment_status");
                o.fulfillmentStatus = rs.getString("fulfillment_status");
                return o;
            }
        }
    }

    private boolean markRefunded(Connection c, long buyerId, long transactionId, String method,
                                 String reference, String reason) throws SQLException {
        String sql = "UPDATE transactions SET payment_status='REFUNDED',fulfillment_status='CANCELLED',"
                + "refund_method=?,refund_reference=?,refund_reason=?,refunded_at=CURRENT_TIMESTAMP "
                + "WHERE txn_id=? AND buyer_id=? AND payment_status='COMPLETED' AND fulfillment_status='AWAITING_PICKUP'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, method); ps.setString(2, reference); ps.setString(3, reason);
            ps.setLong(4, transactionId); ps.setLong(5, buyerId);
            return ps.executeUpdate() == 1;
        }
    }

    private boolean restoreListing(Connection c, long listingId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE listings SET status='AVAILABLE' WHERE listing_id=? AND status='SOLD'")) {
            ps.setLong(1, listingId); return ps.executeUpdate() == 1;
        }
    }

    private void removePoints(Connection c, long buyerId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE students SET sustainability_points=CASE WHEN sustainability_points>=10 THEN sustainability_points-10 ELSE 0 END WHERE student_id=?")) {
            ps.setLong(1, buyerId); ps.executeUpdate();
        }
    }

    private void creditWallet(Connection c, long buyerId, long transactionId, BigDecimal amount,
                              String reference, String title) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE students SET wallet_balance=wallet_balance+? WHERE student_id=?")) {
            ps.setBigDecimal(1, amount); ps.setLong(2, buyerId);
            if (ps.executeUpdate() != 1) throw new SQLException("Buyer wallet not found");
        }
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO wallet_transactions(student_id,transaction_id,entry_type,amount,destination,reference,details) VALUES(?,?,'REFUND_CREDIT',?,'WALLET',?,?)")) {
            ps.setLong(1, buyerId); ps.setLong(2, transactionId); ps.setBigDecimal(3, amount);
            ps.setString(4, reference); ps.setString(5, "Refund for " + title); ps.executeUpdate();
        }
    }

    private void notify(Connection c, long studentId, String message, String path) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO notifications(student_id,message,link_path) VALUES(?,?,?)")) {
            ps.setLong(1, studentId); ps.setString(2, message); ps.setString(3, path); ps.executeUpdate();
        }
    }

    private void notifyWaitlist(Connection c, long listingId, String title) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO notifications(student_id,message,link_path) SELECT student_id,?,? FROM waitlist WHERE listing_id=?")) {
            ps.setString(1, "“" + title + "” is available again. First completed checkout wins.");
            ps.setString(2, "/listing?id=" + listingId); ps.setLong(3, listingId); ps.executeUpdate();
        }
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM waitlist WHERE listing_id=?")) {
            ps.setLong(1, listingId); ps.executeUpdate();
        }
    }

    private String displayPayment(String method) { return "UPI".equals(method) ? "UPI" : "net-banking"; }
    private Result fail(String message) { return new Result(false, message); }
    private void rollback(Connection c) { if (c != null) try { c.rollback(); } catch (SQLException ignored) {} }
    private void close(Connection c) { if (c != null) try { c.close(); } catch (SQLException ignored) {} }
}
