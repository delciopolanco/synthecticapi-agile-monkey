package com.agilemonkeys.syntheticapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AppConfig {

    @Value("${app.admin.emails}")
    private String adminEmails;

    public Set<String> getAdminEmails() {
        return Stream.of(adminEmails.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}