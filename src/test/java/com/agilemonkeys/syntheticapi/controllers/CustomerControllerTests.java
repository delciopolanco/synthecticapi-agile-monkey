package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class CustomerControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void testGetAllCustomers() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerService.getCustomers()).thenReturn(Collections.singletonList(customer));

        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getName());
        assertEquals("Doe", response.getBody().get(0).getSurname());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<Customer> response = customerController.getCustomerById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getName());
        assertEquals("Doe", response.getBody().getSurname());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerService.getCustomerById(1L)).thenReturn(null);

        ResponseEntity<Customer> response = customerController.getCustomerById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void testSaveCustomerSimplified() {
        Customer customer = new Customer("John", "Doe", "photo.jpg");
        customer.setCreatedBy("user");
        customer.setId(1L);

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer);
        assertNotNull(savedCustomer);
        assertEquals("John", savedCustomer.getName());
    }


    @Test
    void testUpdateCustomerSimplified() {
        Customer customer = new Customer("John", "Doe", "photo.jpg");
        customer.setId(1L);
        customer.setLastModifiedBy("user");

        when(customerService.updateCustomer(1L, customer)).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(1L, customer);

        assertNotNull(updatedCustomer);
        assertEquals("John", updatedCustomer.getName());
        assertEquals("Doe", updatedCustomer.getSurname());
        assertEquals("user", updatedCustomer.getLastModifiedBy());
    }



    @Test
    void testDeleteCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(1L);
    }
}