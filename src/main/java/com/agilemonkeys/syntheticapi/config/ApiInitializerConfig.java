package com.agilemonkeys.syntheticapi.config;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.repositories.CustomerRepository;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiInitializerConfig {

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository cutomerRepository, UserRepository userRepository) {
        return args -> {
            // Default Users

            Customer customer1 = new Customer("Delcio", "Polanco", "primary_photo.jpg");
            customer1.setCreatedBy("Admin");
            customer1.setLastModifiedBy("Admin");

            Customer customer2 = new Customer("Jayden", "Polanco", "jayden_photo.jpg");
            customer2.setCreatedBy("Admin");
            customer2.setLastModifiedBy("Admin");

            cutomerRepository.saveAll(List.of(customer1, customer2));
        };
    }
}
