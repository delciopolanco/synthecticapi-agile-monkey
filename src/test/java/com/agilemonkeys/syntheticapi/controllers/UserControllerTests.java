package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.converters.UserDtoConverter;
import com.agilemonkeys.syntheticapi.dtos.UserDto;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserDtoConverter userDtoConverter;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(userDtoConverter.convertToDto(user)).thenReturn(userDto);

        ResponseEntity<List<UserDto>> response = userController.listUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Alice", response.getBody().get(0).getName());
        assertEquals("alice@example.com", response.getBody().get(0).getEmail());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        when(userService.getUserById(1L)).thenReturn(user);
        when(userDtoConverter.convertToDto(user)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getUserById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().getName());
        assertEquals("alice@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(1L)).thenReturn(null);

        ResponseEntity<UserDto> response = userController.getUserById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSaveUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userDtoConverter.convertToEntity(any(UserDto.class))).thenReturn(user);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userDtoConverter.convertToDto(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.createUser(userDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().getName());
        assertEquals("alice@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userDtoConverter.convertToEntity(any(UserDto.class))).thenReturn(user);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);
        when(userDtoConverter.convertToDto(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.updateUser(1L, userDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().getName());
        assertEquals("alice@example.com", response.getBody().getEmail());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }
}