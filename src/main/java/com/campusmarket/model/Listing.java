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

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    /** Illustration file for this listing, chosen from the title/category. */
    public String getImage() {
        String t = title == null ? "" : title.toLowerCase();
        String c = category == null ? "" : category.toLowerCase();
        if (t.contains("calculator")) return "calculator.svg";
        if (t.contains("headphone") || t.contains("earphone") || t.contains("earbud")) return "headphones.svg";
        if (t.contains("lamp")) return "lamp.svg";
        if (t.contains("table") || t.contains("desk") || t.contains("chair")) return "table.svg";
        if (t.contains("drawing") || t.contains("compass") || t.contains("geometry")) return "drawing.svg";
        if (c.contains("book")) return "books.svg";
        if (c.contains("electronic")) return "electronics.svg";
        if (c.contains("furniture")) return "furniture.svg";
        if (c.contains("hostel")) return "hostel.svg";
        if (c.contains("cloth")) return "clothing.svg";
        if (c.contains("sport")) return "sports.svg";
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
