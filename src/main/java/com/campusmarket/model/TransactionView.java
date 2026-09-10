package com.campusmarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * A transaction row enriched with display fields (item title and the name
 * of the other party) for the buyer/seller history screens.
 */
public class TransactionView {
    private long txnId;
    private long listingId;
    private String listingTitle;
    private String counterpartyName; // seller name (buyer view) or buyer name (seller view)
    private BigDecimal amount;
    private Timestamp txnDate;

    public long getTxnId() {
        return txnId;
    }

    public void setTxnId(long txnId) {
        this.txnId = txnId;
    }

    public long getListingId() {
        return listingId;
    }

    public void setListingId(long listingId) {
        this.listingId = listingId;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(Timestamp txnDate) {
        this.txnDate = txnDate;
    }
}
