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
import com.campus.wallet.pojo.ServiceResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;

@RestController
@RequestMapping("/user/account")

public class UserAccountController {
    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/confirmReceipt")
    public ResponseEntity<ServiceResult<Void>> confirmReceipt(@RequestBody ConfirmReceiptRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        ServiceResult<Void> result = userAccountService.confirmReceipt(userID, request.getOrderID());
        
        if (result.getCode() == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ServiceResult<Void>> withdraw(@RequestBody WithdrawRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        ServiceResult<Void> result = userAccountService.withdraw(userID, request.getAmount());
        
        if (result.getCode() == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }


    @PostMapping("/recharge")
    public ResponseEntity<ServiceResult<Void>> recharge(@RequestBody RechargeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        ServiceResult<Void> result = userAccountService.Recharge(userID, request.getAmount());
        
        if (result.getCode() == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }

    @PostMapping("/sellerRefund")
    public ResponseEntity<ServiceResult<Void>> sellerRefund(@RequestBody SellerRefundRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        ServiceResult<Void> result = userAccountService.sellerRefund(userID, request.getOrderID());
        
        if (result.getCode() == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<ServiceResult<Void>> pay(@RequestBody PayRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        ServiceResult<Void> result = userAccountService.Pay(userID, request.getOrderID());
        
        if (result.getCode() == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }
}
