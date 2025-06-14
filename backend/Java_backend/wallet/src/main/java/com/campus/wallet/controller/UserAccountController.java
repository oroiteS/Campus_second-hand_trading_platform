package com.campus.wallet.controller;

import com.campus.wallet.pojo.WithdrawRequest;
import com.campus.wallet.service.UserAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.campus.wallet.pojo.RechargeRequest;
import com.campus.wallet.pojo.SellerRefundRequest;
import com.campus.wallet.pojo.PayRequest;
import com.campus.wallet.pojo.ConfirmReceiptRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.math.BigDecimal;

@RestController
@RequestMapping("/user/account")

public class UserAccountController {
    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/confirmReceipt")
    public void confirmReceipt(@RequestBody ConfirmReceiptRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.confirmReceipt(userID, request.getOrderID());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.withdraw(userID, request.getAmount());
    }


    @PostMapping("/recharge")
    public void recharge(@RequestBody RechargeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.Recharge(userID, request.getAmount());
    }

    @PostMapping("/sellerRefund")
    public void sellerRefund(@RequestBody SellerRefundRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.sellerRefund(userID, request.getOrderID());
    }

    @PostMapping("/pay")
    public void pay(@RequestBody PayRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.Pay(userID, request.getOrderID());
    }
}
