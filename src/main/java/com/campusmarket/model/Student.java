package com.campusmarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** A campus user who can both sell and buy items. */
public class Student {
    private long id;
    private String name;
    private String email;
    private String phone;
    private Timestamp createdAt;
    private BigDecimal walletBalance;
    private int sustainabilityPoints;
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public int getSustainabilityPoints() {
        return sustainabilityPoints;
    }

    public void setSustainabilityPoints(int sustainabilityPoints) {
        this.sustainabilityPoints = sustainabilityPoints;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
