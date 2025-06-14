package com.campus.wallet.dao;

import com.campus.wallet.pojo.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    // 根据用户ID查找用户账户信息
    UserAccount findByUser_ID(String userID);

    // 更新用户余额
    UserAccount save(UserAccount userAccount);

    // 可以添加其他自定义查询方法，例如根据条件查找等
}
