package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.TransactionView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC data-access for transaction history (buyer & seller views). */
public class TransactionDao {

    private TransactionView map(ResultSet rs) throws SQLException {
        TransactionView t = new TransactionView();
        t.setTxnId(rs.getLong("txn_id"));
        t.setListingId(rs.getLong("listing_id"));
        t.setListingTitle(rs.getString("listing_title"));
        t.setCounterpartyName(rs.getString("counterparty"));
        t.setCounterpartyEmail(rs.getString("counterparty_email"));
        t.setCounterpartyPhone(rs.getString("counterparty_phone"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setTxnDate(rs.getTimestamp("txn_date"));
        t.setReviewed(rs.getInt("reviewed") > 0);
        t.setPaymentMethod(rs.getString("payment_method"));
        t.setPaymentReference(rs.getString("payment_reference"));
        t.setPaymentStatus(rs.getString("payment_status"));
        return t;
    }

    /** Items the student BOUGHT (counterparty = seller). */
    public List<TransactionView> purchases(long buyerId) {
        String sql = "SELECT t.txn_id,t.listing_id,t.amount,t.txn_date,t.payment_method,t.payment_reference,t.payment_status,(SELECT COUNT(*) FROM reviews r WHERE r.txn_id=t.txn_id) AS reviewed, "
                + "l.title AS listing_title, seller.name AS counterparty, seller.email AS counterparty_email, seller.phone AS counterparty_phone "
                + "FROM transactions t "
                + "JOIN listings l ON l.listing_id = t.listing_id "
                + "JOIN students seller ON seller.student_id = l.seller_id "
                + "WHERE t.buyer_id = ? ORDER BY t.txn_date DESC";
        return query(sql, buyerId);
    }

    /** Items the student SOLD (counterparty = buyer). */
    public List<TransactionView> sales(long sellerId) {
        String sql = "SELECT t.txn_id,t.listing_id,t.amount,t.txn_date,t.payment_method,t.payment_reference,t.payment_status,(SELECT COUNT(*) FROM reviews r WHERE r.txn_id=t.txn_id) AS reviewed, "
                + "l.title AS listing_title, buyer.name AS counterparty, buyer.email AS counterparty_email, buyer.phone AS counterparty_phone "
                + "FROM transactions t "
                + "JOIN listings l ON l.listing_id = t.listing_id "
                + "JOIN students buyer ON buyer.student_id = t.buyer_id "
                + "WHERE l.seller_id = ? ORDER BY t.txn_date DESC";
        return query(sql, sellerId);
    }

    private List<TransactionView> query(String sql, long id) {
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<TransactionView> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
