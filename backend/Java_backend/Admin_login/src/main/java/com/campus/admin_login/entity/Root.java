package com.campus.admin_login.entity;
/*
*@belongsPackage: com.campus.admin_login.entity
*@author: syn
*@createTime: 2025-06-20
*@description: 管理员映射实体
*@version: 1.0
*/
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Root")
@Data
public class Root {
    @Id
    @Column(name = "Root_id", length = 9)
    private String rootId;

    @Column(name = "password", length = 64, nullable = false)
    private String password;
}
