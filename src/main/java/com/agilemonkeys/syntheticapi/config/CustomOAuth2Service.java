package com.agilemonkeys.syntheticapi.config;


import com.agilemonkeys.syntheticapi.entities.Roles;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private AppConfig appConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("login");
        String name = oAuth2User.getAttribute("name");

        Set<String> adminEmails = appConfig.getAdminEmails();
        boolean isAdmin = adminEmails.contains(email);

        // Determine roles
        Set<Roles> roles = new HashSet<>();
        roles.add(isAdmin ? Roles.ADMIN : Roles.USER); // Default role


        Optional<User> optionalUser = Optional.ofNullable(userService.getUserByEmail(email));
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setName(name);
        user.setRoles(roles);

        if (optionalUser.isPresent()) {
            user.setLastModifiedBy("Admin");
            userService.updateUser(user.getId(), user);
        } else {
            user.setCreatedBy("Admin");
            userService.createUser(user);
        }

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        authorities.addAll(oAuth2User.getAuthorities());

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "name");
    }
}