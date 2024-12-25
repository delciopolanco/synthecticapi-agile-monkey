package com.agilemonkeys.syntheticapi.config;

import com.agilemonkeys.syntheticapi.entities.Roles;
import com.agilemonkeys.syntheticapi.entities.User;
import com.agilemonkeys.syntheticapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserRepository userRepository;

    @Autowired
    private AppConfig appConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Set<String> adminEmails = appConfig.getAdminEmails();
        boolean isAdmin = adminEmails.contains(email);


        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ROLE_USER.toString()));

        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.toString()));
        }

        Set<Roles> roles = authorities.stream()
                .map(authority -> Roles.valueOf(authority.getAuthority()))
                .collect(Collectors.toSet());

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setRoles(roles);
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRoles(roles);
        }
        userRepository.save(user);

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}