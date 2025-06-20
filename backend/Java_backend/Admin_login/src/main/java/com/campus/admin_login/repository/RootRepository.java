package com.campus.admin_login.repository;
/*
*@belongsPackage: com.campus.admin_login.repository
*@author: syn
*@createTime: 2025-06-20
*@description: 数据库映射类
*@version: 1.0
*/
import com.campus.admin_login.entity.Root;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RootRepository extends JpaRepository<Root, String> {
    Optional<Root> findByRootId(String rootId);
}