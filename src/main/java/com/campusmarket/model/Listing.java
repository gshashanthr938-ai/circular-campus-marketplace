package com.campusmarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** An item put up for resale by a student. */
public class Listing {
    private long id;
    private long sellerId;
    private String sellerName;   // populated via join for display
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private String condition;    // maps to column item_condition
    private String status;       // AVAILABLE | SOLD
    private Timestamp createdAt;
    private String imagePath;
    private String moderationNote;
    private double averageRating;
    private int reviewCount;
    private Timestamp sellerMemberSince;
    private int sellerSalesCount;
    private double sellerAverageRating;
    private int sellerReviewCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getModerationNote() { return moderationNote; }
    public void setModerationNote(String moderationNote) { this.moderationNote = moderationNote; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public Timestamp getSellerMemberSince() { return sellerMemberSince; }
    public void setSellerMemberSince(Timestamp sellerMemberSince) { this.sellerMemberSince = sellerMemberSince; }
    public int getSellerSalesCount() { return sellerSalesCount; }
    public void setSellerSalesCount(int sellerSalesCount) { this.sellerSalesCount = sellerSalesCount; }
    public double getSellerAverageRating() { return sellerAverageRating; }
    public void setSellerAverageRating(double sellerAverageRating) { this.sellerAverageRating = sellerAverageRating; }
    public int getSellerReviewCount() { return sellerReviewCount; }
    public void setSellerReviewCount(int sellerReviewCount) { this.sellerReviewCount = sellerReviewCount; }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    /** Reference photograph for curated inventory; illustration fallback for other listings. */
    public String getImage() {
        if (imagePath != null && imagePath.matches("[a-zA-Z0-9_./-]+\\.(jpg|jpeg|png|webp|svg)")) return imagePath;
        String curated = com.campusmarket.db.Catalog.imageFor(title);
        if (curated != null) return curated;
        String t = title == null ? "" : title.toLowerCase();
        String c = category == null ? "" : category.toLowerCase();
        if (t.contains("calculator")) return "photos/calculator.jpg";
        if (t.contains("headphone") || t.contains("earphone") || t.contains("earbud")) return "photos/headphones.jpg";
        if (t.contains("lamp")) return "photos/lamp.jpg";
        if (t.contains("table") || t.contains("desk") || t.contains("chair")) return "photos/desk.jpg";
        if (t.contains("drawing") || t.contains("compass") || t.contains("geometry")) return "photos/pencils.jpg";
        if (c.contains("book")) return "photos/books.jpg";
        if (c.contains("electronic")) return "photos/laptop.jpg";
        if (c.contains("furniture")) return "photos/chair.jpg";
        if (c.contains("hostel")) return "photos/mug.jpg";
        if (c.contains("cloth")) return "clothing.svg";
        if (c.contains("sport")) return "photos/basketball.jpg";
        return "default.svg";
    }

    /** CSS suffix for the condition badge colour (like/good/fair). */
    public String getConditionClass() {
        if (condition == null) return "good";
        String c = condition.toLowerCase();
        if (c.contains("like")) return "like";
        if (c.contains("fair")) return "fair";
        return "good";
    }
}
