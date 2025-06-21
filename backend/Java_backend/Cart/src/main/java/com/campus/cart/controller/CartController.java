package com.campus.cart.controller;

import com.campus.cart.pojo.Commodity;
import com.campus.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-8085/cart")

public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/commodities")
    public ResponseEntity<List<Commodity>> getCartCommodities(
            @RequestParam String userId,
            @RequestParam(defaultValue = "全部") String category) {
        List<Commodity> result;
        if ("全部".equals(category)) {
            result = cartService.getAllCommodities(userId);
        } else {
            result = cartService.getCommoditiesByCategory(userId, category);
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestParam String userId, @RequestParam String commodityId) {
        boolean success = cartService.addToCart(userId, commodityId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        return res;
    }

    @PostMapping("/remove")
    public Map<String, Object> removeFromCart(@RequestParam String userId, @RequestParam String commodityId) {
        boolean success = cartService.removeFromCart(userId, commodityId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        return res;
    }
}

