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

    @PostMapping("/sellerRefund/{userID}")
    public void sellerRefund(@PathVariable String userID) {
        userAccountService.sellerRefund(userID);
    }

    @PostMapping("/adminRefund/{userID}")
    public void adminRefund(@PathVariable String userID) {
        userAccountService.adminRefund(userID);
    }
}
