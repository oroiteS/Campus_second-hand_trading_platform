package com.campus.product_management_seller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * 全局跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 使用allowedOriginPatterns而不是allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带凭证
                .maxAge(3600); // 预检请求缓存时间（秒）
    }
    
    /**
     * CORS配置源 - 统一的CORS配置
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 使用allowedOriginPatterns允许所有域名，支持通配符
        configuration.addAllowedOriginPattern("*");
        
        // 允许所有请求头
        configuration.addAllowedHeader("*");
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("X-Requested-With");
        configuration.addExposedHeader("accept");
        configuration.addExposedHeader("Origin");
        configuration.addExposedHeader("Access-Control-Request-Method");
        configuration.addExposedHeader("Access-Control-Request-Headers");
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Length");
        configuration.addExposedHeader("X-Requested-With");
        
        // 支持图片跨域访问的额外配置
        configuration.addExposedHeader("Cache-Control");
        configuration.addExposedHeader("Content-Disposition");
        configuration.addExposedHeader("Content-Encoding");
        configuration.addExposedHeader("Date");
        configuration.addExposedHeader("ETag");
        configuration.addExposedHeader("Expires");
        configuration.addExposedHeader("Last-Modified");
        configuration.addExposedHeader("Server");
        configuration.addExposedHeader("Vary");
        
        // 设置允许的请求头，支持文件上传
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("X-Requested-With");
        configuration.addAllowedHeader("accept");
        configuration.addAllowedHeader("Origin");
        configuration.addAllowedHeader("Access-Control-Request-Method");
        configuration.addAllowedHeader("Access-Control-Request-Headers");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Cache-Control");
        configuration.addAllowedHeader("Content-Disposition");
        configuration.addAllowedHeader("Content-Range");
        configuration.addAllowedHeader("Range");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}