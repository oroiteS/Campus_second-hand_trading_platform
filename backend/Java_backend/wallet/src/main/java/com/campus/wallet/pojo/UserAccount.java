package com.campus.wallet.pojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("wallet")
public class UserAccount {
    @TableField("user_id")
    @TableId(value = "user_id", type = IdType.INPUT)// 显式指定数据库字段名
    private String userId; // 修改为驼峰命名

    @TableField("money") // 可选，但建议保留以明确映射
    private BigDecimal money;

    public String getUserId() { // 方法名同步修改
        return userId;
    }

    public void setUserId(String userId) { // 方法名同步修改
        this.userId = userId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "UserAccount{" + // 类名拼写修正
                "userId='" + userId + '\'' + // 使用正确的属性名
                ", money=" + money +
                '}';
    }
}
