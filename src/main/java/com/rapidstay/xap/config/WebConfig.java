package com.rapidstay.xap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 👉 Firebase 도메인 등록 (필요 시 여러 개 가능)
                .allowedOrigins(
                        "https://rapidstay-c7f8e.web.app",
                        "https://rapidstay-c7f8e.firebaseapp.com",
                        "http://localhost:8080"
                )
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 캐시 유효 시간 (1시간)
    }
}
