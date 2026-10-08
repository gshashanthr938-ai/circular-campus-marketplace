package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.WalletEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Read-only wallet history access. Balance changes are owned by services. */
public class WalletDao {
    public List<WalletEntry> findFor(long studentId) {
        String sql = "SELECT wallet_txn_id,transaction_id,entry_type,amount,destination,reference,details,created_at "
                + "FROM wallet_transactions WHERE student_id=? ORDER BY created_at DESC,wallet_txn_id DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<WalletEntry> out = new ArrayList<>();
                while (rs.next()) {
                    WalletEntry e = new WalletEntry();
                    e.setId(rs.getLong("wallet_txn_id"));
                    long txnId = rs.getLong("transaction_id");
                    e.setTransactionId(rs.wasNull() ? null : txnId);
                    e.setEntryType(rs.getString("entry_type"));
                    e.setAmount(rs.getBigDecimal("amount"));
                    e.setDestination(rs.getString("destination"));
                    e.setReference(rs.getString("reference"));
                    e.setDetails(rs.getString("details"));
                    e.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(e);
                }
                return out;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
