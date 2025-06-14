package com.campus.wallet.service;
import java.math.BigDecimal;

public interface IUserAccountService {
    // 确认收货，将待转入金额转入余额
    void confirmReceipt(String userID,String orderID);

    // 用户提现
    void withdraw(String userID, BigDecimal amount);

    // 卖家同意退款，将冻结金额转入用户余额
    void sellerRefund(String userID);

    // 管理员强制退款，从冻结金额扣除并返还给用户
    void adminRefund(String userID);
}
