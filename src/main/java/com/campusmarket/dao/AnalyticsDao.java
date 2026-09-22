package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.MarketplaceAnalytics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Aggregated, read-only JDBC queries for the administrator dashboard. */
public class AnalyticsDao {
    public MarketplaceAnalytics load() {
        try (Connection c = Db.getConnection()) {
            MarketplaceAnalytics analytics = new MarketplaceAnalytics();
            analytics.setStudents(count(c, "SELECT COUNT(*) FROM students"));
            analytics.setListings(count(c, "SELECT COUNT(*) FROM listings"));
            analytics.setAvailableListings(count(c, "SELECT COUNT(*) FROM listings WHERE status='AVAILABLE'"));
            analytics.setCompletedTransactions(count(c, "SELECT COUNT(*) FROM transactions WHERE payment_status='COMPLETED'"));
            analytics.setCompletedHandovers(count(c, "SELECT COUNT(*) FROM transactions WHERE fulfillment_status='PICKUP_COMPLETED'"));
            analytics.setWaitlistedStudents(count(c, "SELECT COUNT(*) FROM waitlist"));
            analytics.setTransactionValue(decimal(c, "SELECT COALESCE(SUM(amount),0) FROM transactions WHERE payment_status='COMPLETED'"));

            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*),COALESCE(AVG(rating),0) FROM reviews")) {
                if (rs.next()) {
                    analytics.setReviewCount(rs.getInt(1));
                    analytics.setAverageRating(rs.getDouble(2));
                }
            }
            analytics.setCategories(breakdown(c, "SELECT category,COUNT(*) FROM listings GROUP BY category ORDER BY COUNT(*) DESC,category"));
            analytics.setPaymentMethods(breakdown(c, "SELECT payment_method,COUNT(*) FROM transactions WHERE payment_status='COMPLETED' GROUP BY payment_method ORDER BY COUNT(*) DESC,payment_method"));
            return analytics;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int count(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private BigDecimal decimal(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            BigDecimal value = rs.getBigDecimal(1);
            return value == null ? BigDecimal.ZERO : value;
        }
    }

    private List<MarketplaceAnalytics.Breakdown> breakdown(Connection c, String sql) throws SQLException {
        List<String> labels = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        int maximum = 0;
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                labels.add(rs.getString(1));
                int value = rs.getInt(2);
                counts.add(value);
                maximum = Math.max(maximum, value);
            }
        }
        List<MarketplaceAnalytics.Breakdown> result = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            int percentage = maximum == 0 ? 0 : Math.max(4, (counts.get(i) * 100) / maximum);
            result.add(new MarketplaceAnalytics.Breakdown(labels.get(i), counts.get(i), percentage));
        }
        return result;
    }
}
