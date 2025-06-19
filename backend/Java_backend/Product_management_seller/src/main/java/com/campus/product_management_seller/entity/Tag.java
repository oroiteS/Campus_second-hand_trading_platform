package com.campus.product_management_seller.entity;

import jakarta.persistence.*;

/**
 * Tag实体类，对应数据库中的tags表
 */
@Entity
@Table(name = "tags")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TID")
    private Integer tid;
    
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    
    @Column(name = "tag_Name", nullable = false, length = 20)
    private String tagName;
    
    // 默认构造函数
    public Tag() {}
    
    // 带参构造函数
    public Tag(Integer categoryId, String tagName) {
        this.categoryId = categoryId;
        this.tagName = tagName;
    }
    
    // Getter和Setter方法
    public Integer getTid() {
        return tid;
    }
    
    public void setTid(Integer tid) {
        this.tid = tid;
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getTagName() {
        return tagName;
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    @Override
    public String toString() {
        return "Tag{" +
                "tid=" + tid +
                ", categoryId=" + categoryId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}