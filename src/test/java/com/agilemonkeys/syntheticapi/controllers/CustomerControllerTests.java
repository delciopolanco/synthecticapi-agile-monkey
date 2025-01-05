package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.converters.CustomerDtoConverter;
import com.agilemonkeys.syntheticapi.converters.UserDtoConverter;
import com.agilemonkeys.syntheticapi.dtos.CustomerDto;
import com.agilemonkeys.syntheticapi.dtos.UserDto;
import com.agilemonkeys.syntheticapi.entities.Customer;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.services.CustomerService;
import com.agilemonkeys.syntheticapi.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @Mock
    private FileService fileService;

    @Mock
    private CustomerDtoConverter customerDtoConverter;

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

        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("John");
        customerDto.setSurname("Doe");

        when(customerService.getCustomers()).thenReturn(Collections.singletonList(customer));
        when(customerDtoConverter.convertToDto(customer)).thenReturn(customerDto);

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers();
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

        CustomerDto customerDto = new CustomerDto();
        customer.setId(1L);
        customerDto.setName("John");
        customerDto.setSurname("Doe");

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(customerDtoConverter.convertToDto(customer)).thenReturn(customerDto);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getName());
        assertEquals("Doe", response.getBody().getSurname());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerService.getCustomerById(1L)).thenReturn(null);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSaveCustomerSimplified() {
        Customer customer = new Customer("John", "Doe", "1", "photo.jpg");
        CustomerDto customerDto = new CustomerDto("John", "Doe", "1", "photo.jpg");

        when(customerDtoConverter.convertToEntity(any(CustomerDto.class))).thenReturn(customer);
        when(customerService.saveCustomer(customer)).thenReturn(customer);
        when(customerDtoConverter.convertToDto(any(Customer.class))).thenReturn(customerDto);

        ResponseEntity<CustomerDto> response = customerController.saveCustomer("John", "Doe", "1", null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getName());
        assertEquals("Doe", response.getBody().getSurname());
        assertEquals("1", response.getBody().getCustomerId());
        assertNull(null, response.getBody().getPhoto());
    }

    @Test
    void testUpdateCustomerSimplified() {
        Customer customer = new Customer(1L, "John", "Doe", "1", "photo.jpg");
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "1", "photo.jpg");

        when(customerDtoConverter.convertToEntity(any(CustomerDto.class))).thenReturn(customer);
        when(customerService.updateCustomer(1L, customer)).thenReturn(customer);
        when(customerDtoConverter.convertToDto(any(Customer.class))).thenReturn(customerDto);

        ResponseEntity<CustomerDto> response = customerController.updateCustomer(1L, "John", "Doe", "1", null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("John", response.getBody().getName());
        assertEquals("Doe", response.getBody().getSurname());
        assertEquals("1", response.getBody().getCustomerId());
        assertNull(null, response.getBody().getPhoto());
    }

    @Test
    void testDeleteCustomer() {
        Customer customer = new Customer(1L, "John", "Doe", "1", "photo.jpg");

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(1L);
    }

    @Test
    public void testGetImage() throws IOException {

        Customer customer = new Customer(1L, "John", "Doe", "1", "photo.jpg");

        Path imagePath = Paths.get(FileService.IMAGE_DIR, customer.getPhoto());
        byte[] imageBytes = "image content".getBytes();

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(imagePath)).thenReturn(imageBytes);

            ResponseEntity<byte[]> response = customerController.getImage(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
            assertEquals(imageBytes, response.getBody());
        }
    }

    @Test
    public void testGetImageNotFound() throws IOException {
        when(customerService.getCustomerById(1L)).thenReturn(null);
        ResponseEntity<byte[]> response = customerController.getImage(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateImage() throws IOException {

        Customer customer = new Customer(1L, "John", "Doe", "1", "photo.jpg");
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "1", "photo.jpg");

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "image content".getBytes());
        String fileName = "photo.jpg";

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(fileService.saveImage(file)).thenReturn(fileName);
        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);
        when(customerDtoConverter.convertToDto(customer)).thenReturn(customerDto);

        ResponseEntity<CustomerDto> response = customerController.updateImage(1L, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customerDto, response.getBody());
        verify(customerService).saveCustomer(customer);
    }

    @Test
    public void testUpdateImageNotFound() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "image content".getBytes());

        when(customerService.getCustomerById(1L)).thenReturn(null);

        ResponseEntity<CustomerDto> response = customerController.updateImage(1L, file);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteImage() throws IOException {

        Customer customer = new Customer(1L, "John", "Doe", "1", "photo.jpg");

        Path imagePath = Paths.get(FileService.IMAGE_DIR, customer.getPhoto());

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(imagePath)).thenReturn(true);

            ResponseEntity<Void> response = customerController.deleteImage(1L);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(customerService).saveCustomer(customer);
        }
    }

    @Test
    public void testDeleteImageNotFound() throws IOException {
        when(customerService.getCustomerById(1L)).thenReturn(null);

        ResponseEntity<Void> response = customerController.deleteImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}