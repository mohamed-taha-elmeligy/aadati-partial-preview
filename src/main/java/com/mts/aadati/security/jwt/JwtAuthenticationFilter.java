package com.mts.aadati.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService ;
    private final UserDetailsService userDetailsService ;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver ;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Ignore public authentication paths
        if (isPublicPath(request.getServletPath())){
            filterChain.doFilter(request,response);
            return;
        }

        try {
            // Get Header
            final String authHeader = request.getHeader("Authorization");
            // Check if Authorization Header exists
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            // Get Token from Header
            final String jwtToken = authHeader.substring(7) ;
            // Get User from Token
            final String username = jwtService.extractUsername(jwtToken);
            // Check that the username is not null or SecurityContextHolder is null
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                // Load that the userDetails by username from DB
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // Check that the Token is valid by Token and username
                if (jwtService.isTokenValid(jwtToken,userDetails)){
                    // Generate Authentication Token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("User '{}' authenticated successfully {}", username ,request.getRequestURI());
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Cannot set user authentication: {}", exception.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }


    // ===== Check that the path matches the public path =====
     private boolean isPublicPath(String path){
        List<String> publicPath =  Arrays.asList(
                "/aadati/v1/auth/login",
                "/aadati/v1/auth/register",
                "/swagger-ui"
        );
        return publicPath.stream().anyMatch(path::startsWith);
    }

}
