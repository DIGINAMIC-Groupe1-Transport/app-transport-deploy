package com.diginamic.groupe1.transport.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Rediriger toutes les routes vers index.html pour le routing Angular
        registry.addViewController("/{spring:[^\\.]*}")
                .setViewName("forward:/index.html");
    }

}