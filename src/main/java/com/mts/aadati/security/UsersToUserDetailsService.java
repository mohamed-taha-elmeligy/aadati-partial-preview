package com.mts.aadati.security;

import com.mts.aadati.entities.User;
import com.mts.aadati.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@AllArgsConstructor
@Service
public class UsersToUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsersToUserDetailsService.class);
    private final UserService userService ;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called with Username: {}", username);


        User user = userService.findByUsernameEntity(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with Username: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });


        Set<GrantedAuthority> authorities;
        try {
            authorities = userService.getRolesByUserId(user.getUserId())
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
            if (authorities.isEmpty())
                logger.warn("User {} has no roles assigned", username);
        } catch (Exception e) {
            logger.error("Failed to load roles for user: {}", username, e);
            authorities = new HashSet<>();
        }


        logger.info("loadUserByUsername success for User:(ID) {},(Username) {},(First Name) {},(Last Name) {}",
                user.getUserId() , username, user.getFirstName(),user.getLastName());
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                authorities
        );

    }
}
