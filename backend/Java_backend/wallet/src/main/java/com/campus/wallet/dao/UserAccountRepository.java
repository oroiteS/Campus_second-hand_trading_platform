package com.campus.wallet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.wallet.pojo.UserAccount;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Repository
public interface UserAccountRepository extends BaseMapper<UserAccount> {
    // 根据用户ID查找用户账户信息
    @Select("SELECT * FROM wallet WHERE user_id = #{userId}")
    UserAccount selectByUserId(@Param("userId") String userId);



    // 可以添加其他自定义查询方法，例如根据条件查找等
}
