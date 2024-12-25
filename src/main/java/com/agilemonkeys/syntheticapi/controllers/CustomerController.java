package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.services.CustomerService;
import com.agilemonkeys.syntheticapi.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final FileService fileService;

    @Autowired
    public CustomerController(CustomerService customerService, FileService fileService) {
        this.customerService = customerService;
        this.fileService = fileService;
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
    public ResponseEntity<Customer> saveCustomer(
            @Validated @RequestParam("name") String name,
            @Validated @RequestParam("surname") String surname,
            @Validated @RequestParam("customerId") String customerId,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        String fileName = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                fileName = fileService.saveImage(photo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setCustomerId(customerId);
        customer.setCreatedBy(currentUserName);

        if (fileName != null) {
            customer.setPhoto(fileName);
        }
        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.status(201).body(savedCustomer);
    }

    
    @PutMapping(path="{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable("id") Long id,
            @Validated @RequestParam("name") String name,
            @Validated @RequestParam("surname") String surname,
            @Validated @RequestParam("customerId") String customerId,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {


        String fileName = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                fileName = fileService.saveImage(photo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Customer customer = new Customer(name, surname, customerId, fileName);
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

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null || customer.getPhoto() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Path imagePath = Paths.get(FileService.IMAGE_DIR, customer.getPhoto());
        byte[] imageBytes = Files.readAllBytes(imagePath);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @PutMapping(path="/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> updateImage(@PathVariable Long id,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String fileName = fileService.saveImage(file);
        customer.setPhoto(fileName);
        Customer updatedCustomer = customerService.saveCustomer(customer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) throws IOException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null || customer.getPhoto() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Path imagePath = Paths.get(FileService.IMAGE_DIR, customer.getPhoto());
        Files.deleteIfExists(imagePath);
        customer.setPhoto(null);
        customerService.saveCustomer(customer);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
