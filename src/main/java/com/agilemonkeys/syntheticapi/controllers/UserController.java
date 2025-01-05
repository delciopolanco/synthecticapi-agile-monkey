package com.agilemonkeys.syntheticapi.controllers;

import com.agilemonkeys.syntheticapi.converters.UserDtoConverter;
import com.agilemonkeys.syntheticapi.dtos.UserDto;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.services.UserService;
import com.agilemonkeys.syntheticapi.utils.UserNotFoundExceptions;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @Autowired
    public UserController(@Valid UserService userService, UserDtoConverter userDtoConverter) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.getAllUsers().stream().map(userDtoConverter::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userDtoConverter.convertToDto(user))
                : ResponseEntity.notFound().build();

    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User savedUser = userService.createUser(userDtoConverter.convertToEntity(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoConverter.convertToDto(savedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDtoConverter.convertToEntity(userDto));
        return ResponseEntity.ok(userDtoConverter.convertToDto(updatedUser));
    }

    @PutMapping("/{id}/admin")
    public ResponseEntity<UserDto> changeAdminStatus(@PathVariable Long id, @RequestParam boolean admin) {
        User user = userService.getUserById(id);

        if (user == null) {
            throw new UserNotFoundExceptions(String.format("User with id %s does not exist", id));
        }

        User updatedUser = userService.changeAdminStatus(id, admin);
        UserDto dto = userDtoConverter.convertToDto(updatedUser);

        return ResponseEntity.ok(dto);
    }

}
