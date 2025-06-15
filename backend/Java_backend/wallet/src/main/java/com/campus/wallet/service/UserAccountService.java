package com.campus.wallet.service;

import ch.qos.logback.core.status.Status;
import com.campus.wallet.dao.OrderRepository;
import com.campus.wallet.dao.UserAccountRepository;
import com.campus.wallet.pojo.Order;
import com.campus.wallet.pojo.OrderStatus;
import com.campus.wallet.pojo.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class UserAccountService implements IUserAccountService{
    private final UserAccountRepository userAccountRepository;
    private final OrderRepository orderRepository;


    public UserAccountService(OrderRepository orderRepository, UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void confirmReceipt(String userID,String orderID) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        Order order = orderRepository.selectByOrderId(orderID);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.pending_transaction) {
            throw new IllegalStateException("订单状态错误：当前状态为" + order.getOrderStatus());
        }
        BigDecimal amount = order.getMoney();
        if(userAccount!= null){
            userAccount.setMoney(userAccount.getMoney().add(amount));
            order.setOrderStatus(OrderStatus.completed);
            int updateResult = userAccountRepository.updateById(userAccount);
            int updateOrderResult = orderRepository.updateById(order);
        // 更新订单状态
            if (updateResult != 1) {
                throw new RuntimeException("更新账户余额失败");
            }
            if (updateOrderResult != 1) {
                throw new RuntimeException("更新订单状态失败");
            }

        }
    }

    @Override
    public void withdraw(String userID, BigDecimal amount) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        if (userAccount != null && userAccount.getMoney() != null) {
            BigDecimal currentMoney = userAccount.getMoney();
            if (currentMoney.compareTo(amount) >= 0&&amount.compareTo(BigDecimal.ZERO) > 0) {
                userAccount.setMoney(currentMoney.subtract(amount));
                int updateResult = userAccountRepository.updateById(userAccount);

                if (updateResult != 1) {
                    throw new RuntimeException("更新账户余额失败");
                }
            } else {
                throw new IllegalArgumentException("余额不足或体现金额数据不正确");
            }
        }else{
            throw new IllegalArgumentException("用户不存在或账户余额不足");
        }
    }

    @Override
    public void sellerRefund(String userID,String orderID) {
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        Order order = orderRepository.selectByOrderId(orderID);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.pending_transaction) {
            throw new IllegalStateException("订单状态错误：当前状态为" + order.getOrderStatus());
        }
        BigDecimal amount = order.getMoney();
        if (userAccount != null) {
            userAccount.setMoney(userAccount.getMoney().add(amount));
            order.setOrderStatus(OrderStatus.completed);
            int updateResult = userAccountRepository.updateById(userAccount);
            int updateOrderResult = orderRepository.updateById(order);
            // 更新订单状态
            if (updateResult != 1) {
                throw new RuntimeException("更新账户余额失败");
            }
            if (updateOrderResult != 1) {
                throw new RuntimeException("更新订单状态失败");
            }
        }
    }

    @Override
    public void Pay(String userID,String orderID) {
        UserAccount userAccount=userAccountRepository.selectByUserId(userID);
        Order order=orderRepository.selectByOrderId(orderID);


        if(order == null){
            throw new RuntimeException("订单不存在");
        }
        BigDecimal amount = order.getMoney();
        BigDecimal balance = userAccount.getMoney();
        if (userAccount != null) {
            if(amount.compareTo(balance) < 0){
                userAccount.setMoney(userAccount.getMoney().subtract(amount));
                int updateResult = userAccountRepository.updateById(userAccount);

                if (updateResult != 1) {
                    throw new RuntimeException("更新账户余额失败");
                }
            }else{
                throw new IllegalArgumentException("余额不足");
            }
        }else{
            throw new IllegalArgumentException("用户不存在");
        }
    }

    @Override
    public void Recharge(String userID, BigDecimal amount){
        UserAccount userAccount = userAccountRepository.selectByUserId(userID);
        if (userAccount != null) {
            BigDecimal currentMoney = userAccount.getMoney();
            if(amount.compareTo(BigDecimal.ZERO)>0) {
                userAccount.setMoney(currentMoney.add(amount));
                int updateResult = userAccountRepository.updateById(userAccount);

                if (updateResult != 1) {
                    throw new RuntimeException("更新账户余额失败");
                }
            }else{
                throw new IllegalArgumentException("充值金额必须大于0");
            }
        } else {
            throw new IllegalArgumentException("用户不存在");
        }
    }
}
