package com.campus.product_management_seller.dto;

/**
 * Tag数据传输对象
 */
public class TagDTO {
    
    private Integer tid;
    private String tagName;
    
    // 默认构造函数
    public TagDTO() {}
    
    // 带参构造函数
    public TagDTO(Integer tid, String tagName) {
        this.tid = tid;
        this.tagName = tagName;
    }
    
    // Getter和Setter方法
    public Integer getTid() {
        return tid;
    }
    
    public void setTid(Integer tid) {
        this.tid = tid;
    }
    
    public String getTagName() {
        return tagName;
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    @Override
    public String toString() {
        return "TagDTO{" +
                "tid=" + tid +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}