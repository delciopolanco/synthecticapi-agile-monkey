package com.agilemonkeys.syntheticapi.services;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.repositories.CustomerRepository;
import com.agilemonkeys.syntheticapi.utils.CustomerNotFoundExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCustomers() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        List<Customer> customers = customerService.getCustomers();
        assertEquals(1, customers.size());
        assertEquals("John", customers.get(0).getName());
        assertEquals("Doe", customers.get(0).getSurname());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(1L);
        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getName());
        assertEquals("Doe", foundCustomer.getSurname());
    }

    @Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerRepository.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer);
        assertNotNull(savedCustomer);
        assertEquals("John", savedCustomer.getName());
        assertEquals("Doe", savedCustomer.getSurname());
    }

    @Test
    void testDeleteCustomerHandlesException() {
        // Setup: No customer exists with ID 1
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = assertThrows(CustomerNotFoundExceptions.class, () -> {
            customerService.deleteCustomer(1L); // Call the method that should throw the exception
        });

        // Verify the exception message
        assertEquals("Customer with id 1 does not exist", exception.getMessage());

        // Ensure deleteById is never called
        verify(customerRepository, never()).deleteById(1L);
    }


    @Test
    void testDeleteNonExistentCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty()); // No customer with ID 1

        Exception exception = assertThrows(CustomerNotFoundExceptions.class, () -> {
            customerService.deleteCustomer(1L);
        });

        assertEquals("Customer with id 1 does not exist", exception.getMessage());
    }

}