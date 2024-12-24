package com.agilemonkeys.syntheticapi.services;

import com.agilemonkeys.syntheticapi.entities.Roles;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import com.agilemonkeys.syntheticapi.utils.UserNotFoundExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setName(userDetails.getName());
            existingUser.setRoles(userDetails.getRoles());
            existingUser.setLastModifiedBy(userDetails.getLastModifiedBy());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundExceptions(String.format("User with id %s does not exist", id));
        }
        userRepository.deleteById(id);
    }

    public User changeAdminStatus(Long id, boolean isAdmin) {
        User user = getUserById(id);
        if (user != null) {
            Set<Roles> roles = new HashSet<>();
            roles.add(isAdmin ? Roles.ADMIN : Roles.USER);
            user.setRoles(roles);
            return userRepository.save(user);
        }
        return null;
    }

}
