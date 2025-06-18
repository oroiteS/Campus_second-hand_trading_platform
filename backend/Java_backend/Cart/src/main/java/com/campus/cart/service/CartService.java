package com.campus.cart.service;

import com.campus.cart.pojo.Cart;
import com.campus.cart.pojo.Commodity;

import java.util.List;

public interface CartService {
    List<Commodity> getAllCommodities(String userId);
    List<Commodity> getCommoditiesByCategory(String userId, String categoryName);
    boolean addToCart(String userId, String commodityId);
    boolean removeFromCart(String userId, String commodityId);
    List<Cart> getUserCart(String userId);
}

