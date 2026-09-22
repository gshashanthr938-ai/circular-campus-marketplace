package com.campusmarket.model;

import java.math.BigDecimal;
import java.util.List;

/** Read-only marketplace figures displayed to administrators. */
public class MarketplaceAnalytics {
    private int students;
    private int listings;
    private int availableListings;
    private int completedTransactions;
    private int waitlistedStudents;
    private int reviewCount;
    private double averageRating;
    private BigDecimal transactionValue = BigDecimal.ZERO;
    private List<Breakdown> categories = List.of();
    private List<Breakdown> paymentMethods = List.of();

    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }
    public int getListings() { return listings; }
    public void setListings(int listings) { this.listings = listings; }
    public int getAvailableListings() { return availableListings; }
    public void setAvailableListings(int availableListings) { this.availableListings = availableListings; }
    public int getCompletedTransactions() { return completedTransactions; }
    public void setCompletedTransactions(int completedTransactions) { this.completedTransactions = completedTransactions; }
    public int getWaitlistedStudents() { return waitlistedStudents; }
    public void setWaitlistedStudents(int waitlistedStudents) { this.waitlistedStudents = waitlistedStudents; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public BigDecimal getTransactionValue() { return transactionValue; }
    public void setTransactionValue(BigDecimal transactionValue) { this.transactionValue = transactionValue; }
    public List<Breakdown> getCategories() { return categories; }
    public void setCategories(List<Breakdown> categories) { this.categories = categories; }
    public List<Breakdown> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(List<Breakdown> paymentMethods) { this.paymentMethods = paymentMethods; }

    /** Conservative classroom estimate: each completed resale avoids 2.5 kg CO2e. */
    public double getEstimatedCo2SavedKg() { return completedTransactions * 2.5; }

    public static class Breakdown {
        private final String label;
        private final int count;
        private final int percentage;

        public Breakdown(String label, int count, int percentage) {
            this.label = label;
            this.count = count;
            this.percentage = percentage;
        }

        public String getLabel() { return label; }
        public int getCount() { return count; }
        public int getPercentage() { return percentage; }
    }
}
