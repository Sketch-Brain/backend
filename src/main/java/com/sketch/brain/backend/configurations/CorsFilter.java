package com.sketch.brain.backend.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class CorsFilter implements WebMvcConfigurer {
public class CorsFilter{//Gateway 에 필터 일괄적용으로 인한 삭제.
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
//                .exposedHeaders("*")
//                .maxAge(3000);
//    }
}