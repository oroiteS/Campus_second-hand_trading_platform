package com.campus.wallet.service;
import com.campus.wallet.dao.OrderRepository;
import com.campus.wallet.dao.UserAccountRepository;
import com.campus.wallet.pojo.Order;
import com.campus.wallet.pojo.OrderStatus;
import com.campus.wallet.pojo.ServiceResult;
import com.campus.wallet.pojo.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(rollbackFor=Exception.class)
public class UserAccountService implements IUserAccountService{
    private final UserAccountRepository userAccountRepository;
    private final OrderRepository orderRepository;


    public UserAccountService(OrderRepository orderRepository, UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public ServiceResult<Void> confirmReceipt(String userID,String orderID) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        Order order = orderRepository.selectByOrderId(orderID);
        
        if (order == null) {
            return ServiceResult.error(404, "订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.pending_transaction) {
            return ServiceResult.error(400, "订单状态错误：当前状态为" + order.getOrderStatus());
        }
        if(userAccount == null){
            return ServiceResult.error(404, "用户不存在");
        }
        
        BigDecimal amount = order.getMoney();
        userAccount.setMoney(userAccount.getMoney().add(amount));
        int updateResult = userAccountRepository.updateById(userAccount);
        
        // 更新订单状态
        order.setOrderStatus(OrderStatus.completed);
        int updateOrderResult = orderRepository.updateById(order);
        
        if (updateResult != 1) {
            return ServiceResult.error(500, "更新账户余额失败");
        }
        if (updateOrderResult != 1){
            return ServiceResult.error(500, "更新订单状态失败");
        }
        
        return ServiceResult.success("确认收货成功", null);
    }

    @Override
    public ServiceResult<Void> withdraw(String userID, BigDecimal amount) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        
        if (userAccount == null || userAccount.getMoney() == null) {
            return ServiceResult.error(404, "用户不存在或账户余额不足");
        }
        
        BigDecimal currentMoney = userAccount.getMoney();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.error(400, "提现金额必须大于0");
        }
        if (currentMoney.compareTo(amount) < 0) {
            return ServiceResult.error(400, "余额不足");
        }
        
        userAccount.setMoney(currentMoney.subtract(amount));
        int updateResult = userAccountRepository.updateById(userAccount);
        
        if (updateResult != 1) {
            return ServiceResult.error(500, "更新账户余额失败");
        }
        
        return ServiceResult.success("提现成功", null);
    }

    @Override
    public ServiceResult<Void> sellerRefund(String userID,String orderID) {
        UserAccount userAccount=userAccountRepository.selectByUserId(userID);
        Order order=orderRepository.selectByOrderId(orderID);

        if (order == null) {
            return ServiceResult.error(404, "订单不存在");
        }
        if (userAccount == null) {
            return ServiceResult.error(404, "用户不存在");
        }
        if (order.getOrderStatus() != OrderStatus.pending_transaction) {
            return ServiceResult.error(400, "订单状态错误：当前状态为" + order.getOrderStatus());
        }
        BigDecimal amount = order.getMoney();
        userAccount.setMoney(userAccount.getMoney().add(amount));


        int updateResult = userAccountRepository.updateById(userAccount);
        order.setOrderStatus(OrderStatus.completed);
        int updateOrderResult = orderRepository.updateById(order);
        if (updateResult != 1) {
            return ServiceResult.error(500, "更新账户余额失败");
        }
        if(updateOrderResult != 1){
            return ServiceResult.error(500, "更新订单状态失败");
        }
        return ServiceResult.success("退款成功", null);
    }

    @Override
    public ServiceResult<Void> Pay(String userID, String orderID) {
        UserAccount userAccount=userAccountRepository.selectByUserId(userID);
        Order order=orderRepository.selectByOrderId(orderID);

        if(order == null){
            return ServiceResult.error(404, "订单不存在");
        }
        if (userAccount == null) {
            return ServiceResult.error(404, "用户不存在");
        }
        if (order.getOrderStatus() != OrderStatus.pending_payment) {
            return ServiceResult.error(400, "订单状态错误：当前状态为" + order.getOrderStatus());
        }
        BigDecimal amount = order.getMoney();
        BigDecimal balance = userAccount.getMoney();
        
        if(amount.compareTo(balance) > 0){
            return ServiceResult.error(400, "余额不足");
        }

        userAccount.setMoney(userAccount.getMoney().subtract(amount));
        int updateResult = userAccountRepository.updateById(userAccount);
        order.setOrderStatus(OrderStatus.completed);
        int updateOrderResult = orderRepository.updateById(order);
        if (updateResult != 1) {
            return ServiceResult.error(500, "更新账户余额失败");
        }
        if(updateOrderResult != 1){
            return ServiceResult.error(500, "更新订单状态失败");
        }
        return ServiceResult.success("支付成功", null);
    }

    @Override
    public ServiceResult<Void> Recharge(String userID, BigDecimal amount) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        
        if (userAccount == null) {
            return ServiceResult.error(404, "用户不存在");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.error(400, "充值金额必须大于0");
        }
        
        userAccount.setMoney(userAccount.getMoney().add(amount));
        int updateResult = userAccountRepository.updateById(userAccount);
        
        if (updateResult != 1) {
            return ServiceResult.error(500, "更新账户余额失败");
        }
        
        return ServiceResult.success("充值成功", null);
    }
}
