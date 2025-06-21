//package com.campus.productquery.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Arrays;
//
///**
// * CORS跨域配置
// */
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    /**
//     * 全局跨域配置
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*") // 允许所有域名
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
//                .allowedHeaders("*") // 允许所有请求头
//                .allowCredentials(true) // 允许携带凭证
//                .maxAge(3600); // 预检请求缓存时间（秒）
//    }
//
//    /**
//     * CORS配置源
//     */
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // 允许所有域名进行跨域调用
//        configuration.addAllowedOriginPattern("*");
//
//        // 允许所有请求头
//        configuration.addAllowedHeader("*");
//
//        // 允许的HTTP方法
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // 允许携带凭证
//        configuration.setAllowCredentials(true);
//
//        // 预检请求的缓存时间（秒）
//        configuration.setMaxAge(3600L);
//
//        // 暴露的响应头
//        configuration.addExposedHeader("Content-Type");
//        configuration.addExposedHeader("X-Requested-With");
//        configuration.addExposedHeader("accept");
//        configuration.addExposedHeader("Origin");
//        configuration.addExposedHeader("Access-Control-Request-Method");
//        configuration.addExposedHeader("Access-Control-Request-Headers");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    /**
//     * CORS过滤器
//     */
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter(corsConfigurationSource());
//    }
//}