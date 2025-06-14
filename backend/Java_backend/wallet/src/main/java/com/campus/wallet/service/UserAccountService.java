package com.campus.wallet.service;

import com.campus.wallet.dao.OrderRepository;
import com.campus.wallet.dao.UserAccountRepository;
import com.campus.wallet.pojo.Order;
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
        UserAccount userAccount = userAccountRepository.findByUser_ID(userID);
        Order order = orderRepository.findByOrder_id(orderID);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        BigDecimal amount = order.getMoney();
        if(userAccount!= null){
            userAccount.setMoney(userAccount.getMoney().add(amount));
            userAccountRepository.save(userAccount);
        }
    }

    @Override
    public void withdraw(String userID, BigDecimal amount) {
        UserAccount userAccount = userAccountRepository.findByUser_ID(userID);
        if (userAccount != null && userAccount.getMoney() != null) {
            BigDecimal currentMoney = userAccount.getMoney();
            if (currentMoney.compareTo(amount) >= 0) {
                userAccount.setMoney(currentMoney.subtract(amount));
                userAccountRepository.save(userAccount);
            } else {
                throw new IllegalArgumentException("余额不足");
            }
        }else{
            throw new IllegalArgumentException("用户不存在或账户余额不足");
        }
    }

    @Override
    public void sellerRefund(String userID,String orderID) {
        UserAccount userAccount=userAccountRepository.findByUser_ID(userID);
        Order order=orderRepository.findByOrder_id(orderID);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        BigDecimal amount = order.getMoney();
        if (userAccount != null) {
            userAccount.setMoney(userAccount.getMoney().add(amount));
            userAccountRepository.save(userAccount);
        }
    }

    @Override
    public void Pay(String userID,String orderID) {
        UserAccount userAccount=userAccountRepository.findByUser_ID(userID);
        Order order=orderRepository.findByOrder_id(orderID);

        if(order == null){
            throw new RuntimeException("订单不存在");
        }
        BigDecimal amount = order.getMoney();
        BigDecimal balance = userAccount.getMoney();
        if (userAccount != null) {
            if(amount.compareTo(balance) < 0){
                userAccount.setMoney(userAccount.getMoney().subtract(amount));
                userAccountRepository.save(userAccount);
            }else{
                throw new IllegalArgumentException("余额不足");
            }
        }else{
            throw new IllegalArgumentException("用户不存在");
        }
    }

    @Override
    public void Recharge(String userID, BigDecimal amount){
        UserAccount userAccount = userAccountRepository.findByUser_ID(userID);
        if (userAccount != null) {
            BigDecimal currentMoney = userAccount.getMoney();
            userAccount.setMoney(currentMoney.add(amount));
            userAccountRepository.save(userAccount);
        }else{
            throw new IllegalArgumentException("用户不存在");
        }
    }
}
