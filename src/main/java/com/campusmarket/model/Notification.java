package com.campusmarket.model;

import java.sql.Timestamp;

public class Notification {
    private long id;
    private String message;
    private String linkPath;
    private boolean read;
    private Timestamp createdAt;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLinkPath() { return linkPath; }
    public void setLinkPath(String linkPath) { this.linkPath = linkPath; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
