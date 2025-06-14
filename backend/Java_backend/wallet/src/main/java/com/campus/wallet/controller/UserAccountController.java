package com.campus.wallet.controller;

import com.campus.wallet.pojo.WithdrawRequest;
import com.campus.wallet.service.UserAccountService;
import org.springframework.web.bind.annotation.*;

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
        userAccountService.confirmReceipt(request.getUserID(), request.getOrderID());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawRequest request) {
        userAccountService.withdraw(request.getUserId(), request.getAmount());
    }

    import com.campus.wallet.pojo.RechargeRequest;
    import com.campus.wallet.pojo.SellerRefundRequest;
    import com.campus.wallet.pojo.PayRequest;

    @PostMapping("/recharge")
    public void recharge(@RequestBody RechargeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        userAccountService.recharge(userID, request.getAmount(), request.getPaymentMethod());
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
        userAccountService.pay(userID, request.getOrderID());
    }

    
}
