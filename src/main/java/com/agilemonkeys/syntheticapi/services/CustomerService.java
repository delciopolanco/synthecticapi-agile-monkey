package com.agilemonkeys.syntheticapi.services;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.utils.CustomerNotFoundExceptions;
import com.agilemonkeys.syntheticapi.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer saveCustomer(Customer customer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        customer.setCreatedBy(currentUserName);
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        boolean customerExist = customerRepository.existsById(id);

        if (!customerExist) {
            throw new CustomerNotFoundExceptions(String.format("Customer with id %s does not exist", id));
        }

        customerRepository.deleteById(id);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        customerDetails.setLastModifiedBy(currentUserName);

        Customer existingCustomer = getCustomerById(id);

        if (existingCustomer == null) {
            throw new CustomerNotFoundExceptions(String.format("Customer with id %s does not exist", id));
        }

        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setSurname(customerDetails.getSurname());
        existingCustomer.setPhoto(customerDetails.getPhoto());
        return customerRepository.save(existingCustomer);
    }
}
