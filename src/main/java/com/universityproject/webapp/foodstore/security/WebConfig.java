package com.universityproject.webapp.foodstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ إعداد CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // يمكنك تخصيصه مثل "http://localhost:3000"
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    // ✅ إعداد عرض الصور من مجلد Images
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imagePath = System.getProperty("user.dir") + File.separator + "Images" + File.separator;

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath);
    }


}
