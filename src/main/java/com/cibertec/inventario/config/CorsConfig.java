package com.cibertec.inventario.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/") // Aplica a todos nuestros endpoints
                        .allowedOrigins("http://localhost:4200") // El puerto exacto de Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodos HTTP permitidos
                        .allowedHeaders("*") // Permite cualquier cabecera (util cuando agregues JWT despues)
                        .allowCredentials(true); // Permite el envio de cookies o credenciales de autenticacion
            }
        };
    }
}
