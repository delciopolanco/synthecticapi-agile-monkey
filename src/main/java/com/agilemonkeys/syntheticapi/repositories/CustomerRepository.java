package com.agilemonkeys.syntheticapi.repositories;

import com.agilemonkeys.syntheticapi.entities.Customer;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  Optional<Customer> findByCustomerId(String customerId);
}
