package com.agilemonkeys.syntheticapi.services;

import com.agilemonkeys.syntheticapi.entities.Roles;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import com.agilemonkeys.syntheticapi.utils.UserNotFoundExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User createUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        User createdUser = userRepository.save(user);
        createdUser.setCreatedBy(currentUserName);
        return createdUser;
    }

    public User updateUser(Long id, User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setName(userDetails.getName());
            existingUser.setRoles(userDetails.getRoles());
            existingUser.setLastModifiedBy(currentUserName);
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
            roles.add(isAdmin ? Roles.ROLE_ADMIN : Roles.ROLE_USER);
            user.setRoles(roles);
            return userRepository.save(user);
        }
        return null;
    }

}
