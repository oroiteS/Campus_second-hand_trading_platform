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
        if(userAccount!= null && userAccount.getPending_income() != null){
            userAccount.setMoney(userAccount.getMoney().add(amount));
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
    public void sellerRefund(String userID) {
        UserAccount userAccount = userAccountRepository.findByUser_ID(userID);
        if (userAccount != null && userAccount.getPending_income() != null) {
            BigDecimal pendingIncome = userAccount.getPending_income();
            if (userAccount.getMoney() == null) {
                userAccount.setMoney(pendingIncome);
            } else {
                userAccount.setMoney(userAccount.getMoney().add(pendingIncome));
            }
            userAccount.setPending_income(BigDecimal.ZERO);
            userAccountRepository.save(userAccount);
        }
    }

    @Override
    public void adminRefund(String userID) {
        UserAccount userAccount = userAccountRepository.findByUser_ID(userID);
        if (userAccount != null && userAccount.getPending_income() != null) {
            BigDecimal pendingIncome = userAccount.getPending_income();
            if (userAccount.getMoney() == null) {
                userAccount.setMoney(pendingIncome);
            } else {
                userAccount.setMoney(userAccount.getMoney().add(pendingIncome));
            }
            userAccount.setPending_income(BigDecimal.ZERO);
            userAccountRepository.save(userAccount);
        }
    }
}
