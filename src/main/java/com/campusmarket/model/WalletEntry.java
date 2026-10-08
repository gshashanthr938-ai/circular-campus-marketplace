package com.campusmarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** One immutable credit or withdrawal in a student's refund wallet. */
public class WalletEntry {
    private long id;
    private Long transactionId;
    private String entryType;
    private BigDecimal amount;
    private String destination;
    private String reference;
    private String details;
    private Timestamp createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public boolean isCredit() { return amount != null && amount.signum() > 0; }
}
