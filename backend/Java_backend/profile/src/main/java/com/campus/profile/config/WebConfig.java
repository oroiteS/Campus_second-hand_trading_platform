package com.campus.profile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置静态资源访问路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.avatar-path:./uploads/avatars/}")
    private String avatarUploadPath;

    /**
     * 配置静态资源处理器
     * 将URL路径映射到本地文件系统路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像文件访问路径
        // URL访问路径：/uploads/avatars/**
        // 映射到本地路径：avatarUploadPath
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + avatarUploadPath);
        
        // 可以添加其他静态资源配置
        // 例如：商品图片、文档等
        // registry.addResourceHandler("/uploads/products/**")
        //         .addResourceLocations("file:./uploads/products/");
    }
}