package com.campus.wallet.pojo;

public class ConfirmReceiptRequest {
    private String orderID;
    private String UserID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String receiptID) {
        this.UserID = receiptID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}