package com.agilemonkeys.syntheticapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2Service customOAuth2Service;

    public SecurityConfig(CustomOAuth2Service customOAuth2Service) {
        this.customOAuth2Service = customOAuth2Service;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/v1/customer/**").hasAnyRole("USER", "ADMIN");
            auth.requestMatchers("/api/v1/user/**").hasRole("ADMIN");
            auth.anyRequest().authenticated();
        }).oauth2Login(oauth2Login -> oauth2Login.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2Service))).formLogin(withDefaults());

        return http.build();
    }
}
