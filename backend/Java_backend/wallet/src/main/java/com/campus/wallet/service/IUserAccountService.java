package com.campus.wallet.service;
import com.campus.wallet.pojo.ServiceResult;
import java.math.BigDecimal;

public interface IUserAccountService {
    // 确认收货，将待转入金额转入余额
    ServiceResult<Void> confirmReceipt(String userID,String orderID);

    // 用户提现
    ServiceResult<Void> withdraw(String userID, BigDecimal amount);

    // 卖家同意退款，将冻结金额转入用户余额
    ServiceResult<Void> sellerRefund(String userID,String orderID);

    // 支付
    ServiceResult<Void> Pay(String userID,String orderID);

    // 充值
    ServiceResult<Void> Recharge(String userID, BigDecimal amount);
}
