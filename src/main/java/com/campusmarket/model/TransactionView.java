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
    private String counterpartyEmail;
    private String counterpartyPhone;
    private BigDecimal amount;
    private Timestamp txnDate;
    private boolean reviewed;
    private String paymentMethod;
    private String paymentReference;
    private String paymentStatus;
    private String handoverCode;
    private String fulfillmentStatus;
    private Timestamp pickupCompletedAt;

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

    public String getCounterpartyEmail() { return counterpartyEmail; }
    public void setCounterpartyEmail(String counterpartyEmail) { this.counterpartyEmail = counterpartyEmail; }
    public String getCounterpartyPhone() { return counterpartyPhone; }
    public void setCounterpartyPhone(String counterpartyPhone) { this.counterpartyPhone = counterpartyPhone; }

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
    public boolean isReviewed(){return reviewed;}
    public void setReviewed(boolean reviewed){this.reviewed=reviewed;}
    public String getPaymentMethod(){return paymentMethod;}
    public void setPaymentMethod(String paymentMethod){this.paymentMethod=paymentMethod;}
    public String getPaymentReference(){return paymentReference;}
    public void setPaymentReference(String paymentReference){this.paymentReference=paymentReference;}
    public String getPaymentStatus(){return paymentStatus;}
    public void setPaymentStatus(String paymentStatus){this.paymentStatus=paymentStatus;}
    public String getHandoverCode(){return handoverCode;}
    public void setHandoverCode(String handoverCode){this.handoverCode=handoverCode;}
    public String getFulfillmentStatus(){return fulfillmentStatus;}
    public void setFulfillmentStatus(String fulfillmentStatus){this.fulfillmentStatus=fulfillmentStatus;}
    public Timestamp getPickupCompletedAt(){return pickupCompletedAt;}
    public void setPickupCompletedAt(Timestamp pickupCompletedAt){this.pickupCompletedAt=pickupCompletedAt;}
    public boolean isPickupCompleted(){return "PICKUP_COMPLETED".equals(fulfillmentStatus);}
}
