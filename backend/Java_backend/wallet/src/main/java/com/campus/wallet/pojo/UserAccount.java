package com.campus.wallet.pojo;
import jakarta.persistence.Id;
import java.math.BigDecimal;

public class UserAccount {
    @Id
    private String User_ID;
    private BigDecimal pending_income;
    private BigDecimal money;

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public BigDecimal getPending_income() {
        return pending_income;
    }

    public void setPending_income(BigDecimal pending_income) {
        this.pending_income = pending_income;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "UserAcount{" +
                "User_ID='" + User_ID + '\'' +
                ", pending_income=" + pending_income +
                ", money=" + money +
                '}';
    }
}
