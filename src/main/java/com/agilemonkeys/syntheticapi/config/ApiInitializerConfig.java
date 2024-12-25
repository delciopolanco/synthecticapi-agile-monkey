package com.agilemonkeys.syntheticapi.config;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.repositories.CustomerRepository;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class ApiInitializerConfig {

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository cutomerRepository, UserRepository userRepository) {
        return args -> {
            // Default Users

            Customer customer1 = new Customer("Delcio", "Polanco", "0011898881");
            customer1.setCreatedBy("Admin");
            customer1.setLastModifiedBy("Admin");

            Optional<Customer> existingCustomer = cutomerRepository.findById(1L);

            if (existingCustomer.isEmpty()) {
                cutomerRepository.saveAll(List.of(customer1));
            }
        };
    }
}
