package com.campusmarket.service;

import com.campusmarket.db.Db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

/** Moves refund money out of the wallet using an atomic conditional debit. */
public class WalletService {
    private static final Set<String> METHODS = Set.of("UPI", "NET_BANKING");

    public static class Result {
        public final boolean success;
        public final String message;
        Result(boolean success, String message) { this.success = success; this.message = message; }
    }

    public Result withdraw(long studentId, String rawAmount, String method, String destinationDetail) {
        BigDecimal amount;
        try { amount = new BigDecimal(rawAmount == null ? "" : rawAmount).setScale(2, RoundingMode.UNNECESSARY); }
        catch (Exception e) { return fail("Enter a valid withdrawal amount with up to two decimal places."); }
        if (amount.signum() <= 0 || amount.compareTo(new BigDecimal("100000.00")) > 0)
            return fail("Withdrawal amount must be between ₹0.01 and ₹1,00,000.");
        if (!METHODS.contains(method)) return fail("Choose UPI or net banking for withdrawal.");
        String detail = destinationDetail == null ? "" : destinationDetail.trim();
        if ("UPI".equals(method) && !detail.matches("[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}"))
            return fail("Enter a valid UPI ID, for example name@bank.");
        if ("NET_BANKING".equals(method) && !Set.of("SBI", "HDFC", "ICICI", "AXIS", "KOTAK", "OTHER").contains(detail))
            return fail("Select a bank for withdrawal.");

        Connection c = null;
        try {
            c = Db.getConnection(); c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("UPDATE students SET wallet_balance=wallet_balance-? WHERE student_id=? AND wallet_balance>=?")) {
                ps.setBigDecimal(1, amount); ps.setLong(2, studentId); ps.setBigDecimal(3, amount);
                if (ps.executeUpdate() != 1) { c.rollback(); return fail("Your wallet does not have enough refund balance."); }
            }
            String reference = "WD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO wallet_transactions(student_id,entry_type,amount,destination,reference,details) VALUES(?,'WITHDRAWAL',?,?,?,?)")) {
                ps.setLong(1, studentId); ps.setBigDecimal(2, amount.negate()); ps.setString(3, method);
                ps.setString(4, reference); ps.setString(5, "UPI".equals(method) ? "Withdrawal to " + maskUpi(detail) : "Withdrawal through " + detail + " net banking");
                ps.executeUpdate();
            }
            c.commit();
            return new Result(true, "Withdrawal of ₹" + amount.toPlainString() + " initiated. Reference " + reference + ".");
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Wallet withdrawal failed", e);
        } finally {
            if (c != null) try { c.close(); } catch (SQLException ignored) {}
        }
    }

    private Result fail(String message) { return new Result(false, message); }
    private String maskUpi(String upi) {
        int at = upi.indexOf('@');
        String name = upi.substring(0, at);
        String visible = name.length() <= 2 ? name.substring(0, 1) : name.substring(0, 2);
        return visible + "***" + upi.substring(at);
    }
}
