package com.example.gloomgate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disabling CSRF for simplicity in this example
                .authorizeRequests()
                .requestMatchers("/api/users/register", "/api/users/login","/websocket-endpoint/**").permitAll()  // Public endpoints
                .anyRequest().authenticated()  // All other endpoints require authentication
                .and()
                .httpBasic();  // Using basic authentication for this example
        return http.build();
    }
}
