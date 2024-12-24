package com.agilemonkeys.syntheticapi.controllers;


import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(@Valid UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

         User savedUser = userService.createUser(user);
         savedUser.setCreatedBy(currentUserName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        user.setLastModifiedBy(currentUserName);

        return ResponseEntity.ok(userService.updateUser(id, user));
    }


    @PutMapping("/{id}/admin")
    public ResponseEntity<User> changeAdminStatus(@PathVariable Long id, @RequestParam boolean admin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        User user = userService.getUserById(id);
        if (user.getUsername().equals(currentUserName)) {
            SecurityContextHolder.clearContext();
        }

        User updatedUser = userService.changeAdminStatus(id, admin);
        return ResponseEntity.ok(updatedUser);
    }
}
