package com.example.gateway.config;//package com.example.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.*;
//
//@Configuration
//public class GateWayConfig {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        UrlBasedCorsConfigurationSource ubcs = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addAllowedOriginPattern("*");
//        corsConfiguration.setAllowCredentials(true);
//
//        ubcs.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsWebFilter(ubcs);
//    }
//}
