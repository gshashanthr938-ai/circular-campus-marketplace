package com.campusmarket.model;

import java.math.BigDecimal;

/** A campus user who can both sell and buy items. */
public class Student {
    private long id;
    private String name;
    private String email;
    private BigDecimal walletBalance;
    private int sustainabilityPoints;

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
}
