package com.campus.cart.service;

import com.campus.cart.dao.CartMapper;
import com.campus.cart.dao.CommodityMapper;
import com.campus.cart.pojo.Cart;
import com.campus.cart.pojo.Commodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CommodityMapper commodityMapper;

    @Override
    public List<Commodity> getAllCommodities(String userId) {
        List<String> commodityIds = cartMapper.findCommodityIdsByUserId(userId);
        if (commodityIds == null || commodityIds.isEmpty()) {
            return Collections.emptyList();
        }
        return commodityMapper.getAllCommodities(commodityIds);
    }

    @Override
    public List<Commodity> getCommoditiesByCategory(String userId, String categoryName) {
        List<String> commodityIds = cartMapper.findCommodityIdsByUserId(userId);
        if (commodityIds == null || commodityIds.isEmpty()) {
            return Collections.emptyList();
        }
        if ("全部".equals(categoryName)) {
            return commodityMapper.getAllCommodities(commodityIds);
        }
        Integer categoryId = commodityMapper.getCategoryIdByName(categoryName);
        if (categoryId == null) {
            return Collections.emptyList();
        }
        return commodityMapper.getCommoditiesByCategory(commodityIds, categoryId);
    }

    @Override
    public boolean addToCart(String userId, String commodityId) {
        Cart cart = new Cart();
        cart.setCartId(UUID.randomUUID().toString().substring(0, 10));
        cart.setUserId(userId);
        cart.setCommodityId(commodityId);
        return cartMapper.addToCart(cart) > 0;
    }

    @Override
    public boolean removeFromCart(String userId, String commodityId) {
        return cartMapper.removeFromCart(userId, commodityId) > 0;
    }

    @Override
    public List<Cart> getUserCart(String userId) {
        return cartMapper.getCartByUserId(userId);
    }
}

