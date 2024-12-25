package com.agilemonkeys.syntheticapi.services;

import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals("alice@example.com", users.get(0).getEmail());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);
        assertNotNull(foundUser);
        assertEquals("Alice", foundUser.getName());
        assertEquals("alice@example.com", foundUser.getEmail());
    }


    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.createUser(user);
        assertNotNull(savedUser);
        assertEquals("Alice", savedUser.getName());
        assertEquals("alice@example.com", savedUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Alice");
        existingUser.setEmail("alice@example.com");

        User updatedUser = new User();
        updatedUser.setName("Alice Updated");
        updatedUser.setEmail("alice.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(1L, updatedUser);
        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.updated@example.com", result.getEmail());
    }
}