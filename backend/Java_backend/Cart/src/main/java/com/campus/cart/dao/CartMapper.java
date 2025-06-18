package com.campus.cart.dao;

import com.campus.cart.pojo.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    // 查找用户购物车中所有商品ID
    @Select("SELECT commodity_id FROM cart WHERE User_ID = #{userId}")
    List<String> findCommodityIdsByUserId(String userId);

    @Select("SELECT * FROM cart WHERE User_ID = #{userId}")
    List<Cart> getCartByUserId(@Param("userId") String userId);

    @Insert("INSERT INTO cart (Cart_ID, User_ID, commodity_id) VALUES (#{cartId}, #{userId}, #{commodityId})")
    int addToCart(Cart cart);

    @Delete("DELETE FROM cart WHERE User_ID = #{userId} AND commodity_id = #{commodityId}")
    int removeFromCart(@Param("userId") String userId, @Param("commodityId") String commodityId);

}

