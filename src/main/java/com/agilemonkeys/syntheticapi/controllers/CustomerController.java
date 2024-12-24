package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getCustomers();
        return ResponseEntity.ok(customers);
    }


    @GetMapping(path = "{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("customerId") Long id) {
        Customer customer = customerService.getCustomerById(id);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> saveCustomers(
            @RequestPart("name") String name,
            @RequestPart("surname") String surname,
            @RequestPart("photo") MultipartFile photo
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Customer customer = new Customer(name, surname, photo.getOriginalFilename());
        customer.setCreatedBy(currentUserName);

        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PutMapping(path = "{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable("customerId") Long id,
            @RequestPart("name") String name,
            @RequestPart("surname") String surname,
            @RequestPart("photo") MultipartFile photo
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Customer customer = new Customer(name, surname, photo.getOriginalFilename());
        customer.setLastModifiedBy(currentUserName);

        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(updatedCustomer);
    }


    @DeleteMapping(path = "{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("customerId") Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
