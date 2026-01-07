package com.example.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Frontend Integration
 * Allows cross-origin requests from frontend applications
 */
@Configuration
public class CorsConfig {

        @Bean
        public CorsFilter customCorsFilter() {
                CorsConfiguration config = new CorsConfiguration();

                // Allow credentials (cookies, authorization headers)
                config.setAllowCredentials(true);

                // Allowed origins - Frontend URL
                config.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:3000" // React/Vite frontend
                ));

                // Allowed HTTP methods
                config.setAllowedMethods(Arrays.asList(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"));

                // Allowed headers
                config.setAllowedHeaders(Arrays.asList(
                                "Origin",
                                "Content-Type",
                                "Accept",
                                "Authorization",
                                "X-Requested-With",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers"));

                // Exposed headers (headers that frontend can read)
                config.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin",
                                "Access-Control-Allow-Credentials",
                                "Content-Type",
                                "Authorization"));

                // Max age for preflight requests (in seconds)
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return new CorsFilter(source);
        }
}
