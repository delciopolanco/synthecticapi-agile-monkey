package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.converters.CustomerDtoConverter;
import com.agilemonkeys.syntheticapi.dtos.CustomerDto;
import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.services.CustomerService;
import com.agilemonkeys.syntheticapi.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final FileService fileService;
    private final CustomerDtoConverter customerDtoConverter;

    @Autowired
    public CustomerController(CustomerService customerService, FileService fileService,
            CustomerDtoConverter customerDtoConverter) {
        this.customerService = customerService;
        this.fileService = fileService;
        this.customerDtoConverter = customerDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getCustomers().stream().map(customerDtoConverter::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    @GetMapping(path = "{customerId}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable("customerId") Long id) {
        Customer customer = customerService.getCustomerById(id);
        return customer != null ? ResponseEntity.ok(customerDtoConverter.convertToDto(customer))
                : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerDto> saveCustomer(
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

        Customer customer = new Customer(name, surname, customerId);

        if (fileName != null) {
            customer.setPhoto(fileName);
        }
        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.status(201).body(customerDtoConverter.convertToDto(savedCustomer));
    }

    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerDto> updateCustomer(
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

        Customer customer = new Customer(name, surname, customerId, fileName);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(customerDtoConverter.convertToDto(updatedCustomer));
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

    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerDto> updateImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String fileName = fileService.saveImage(file);
        customer.setPhoto(fileName);
        Customer updatedCustomer = customerService.saveCustomer(customer);
        return new ResponseEntity<>(customerDtoConverter.convertToDto(updatedCustomer), HttpStatus.OK);
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
