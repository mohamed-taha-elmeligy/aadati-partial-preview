package com.mts.aadati.security;

import com.mts.aadati.security.jwt.JwtAccessDeniedHandler;
import com.mts.aadati.security.jwt.JwtAuthenticationEntryPoint;
import com.mts.aadati.security.jwt.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@AllArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String ADMIN = "ADMIN" ;
    private static final String USER = "USER" ;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (Cross-Site Request Forgery) protection for APIs
                .csrf(AbstractHttpConfigurer::disable)
                // CORS (Cross-Origin Resource Sharing) configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Every request is considered stateless and not session dependent.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Authorize configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Authentication Controller
                        .requestMatchers("/aadati/v1/auth/**").permitAll()
                        // User Controller - Admin-only endpoints
                        .requestMatchers("/aadati/v1/user/all").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/search/firstName/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/search/lastName/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/search/fullName/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/recent").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/search/createdAt/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/search/updatedAt/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/role/**").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/withRoles").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/user/count/role/**").hasRole(ADMIN)
                        // User Controller - endpoints accessible by USER and ADMIN
                        .requestMatchers("/aadati/v1/user/**").hasAnyRole(USER, ADMIN)
                        // TaskPriorityLevelController
                        // Admin-only endpoints (add/update/delete)
                        .requestMatchers("/aadati/v1/task-priority-level/add").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/update").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/remove/**").hasRole(ADMIN)
                        // Show/Query endpoints accessible to USER + ADMIN
                        .requestMatchers("/aadati/v1/task-priority-level/all").hasAnyRole(USER,ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/colors").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/asc").hasAnyRole(USER,ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/desc").hasAnyRole(USER,ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/search/**").hasAnyRole(USER,ADMIN)
                        .requestMatchers("/aadati/v1/task-priority-level/exists/**").hasAnyRole(USER,ADMIN)
                        // TaskCompletionController
                        .requestMatchers("/api/v1/task-completions/**").hasAnyRole(USER, ADMIN)
                        // RoleController
                        // Admin-only endpoints
                        .requestMatchers("/aadati/v1/roles/add").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/roles/add-list").hasRole(ADMIN)
                        .requestMatchers("/aadati/v1/roles/delete/**").hasRole(ADMIN)
                        // Show/Query endpoints accessible to USER + ADMIN
                        .requestMatchers("/aadati/v1/roles/search/**").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/aadati/v1/roles/active").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/aadati/v1/roles/deleted").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/aadati/v1/roles/count/active").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/aadati/v1/roles/exists/**").hasAnyRole(USER, ADMIN)
                        // PercentageWeekController
                        .requestMatchers("/percentage-week/**").hasAnyRole(USER, ADMIN)
                        // PercentageDayController
                        .requestMatchers("/percentage-day/**").hasAnyRole(USER, ADMIN)
                        // HabitWeekController
                        .requestMatchers("/aadati/v1/habit-weeks/**").hasAnyRole(USER, ADMIN)
                        // HabitTaskController
                        .requestMatchers("/habit-task/**").hasAnyRole(USER, ADMIN)
                        // HabitDayWeekController
                        .requestMatchers("/aadati/v1/day-of-week/**").hasAnyRole(USER, ADMIN)
                        // HabitController
                        .requestMatchers("/habit/**").hasAnyRole(USER, ADMIN)
                        // HabitCompletionController
                        .requestMatchers("/api/v1/habit-completions/**").hasAnyRole(USER, ADMIN)
                        // HabitCategoryController
                        .requestMatchers(HttpMethod.GET, "/aadati/v1/habit-category/**").hasAnyRole(USER, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/aadati/v1/habit-category/**").hasRole(ADMIN)
                        // HabitCalendarController
                        .requestMatchers("/aadati/v1/habit-calendar/**").hasAnyRole(USER, ADMIN)


                        .anyRequest().authenticated()
                )

                // Add JWT Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Exception Handel
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager (
            AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Any header is allowed in the request "*" means all headers are allowed.
        configuration.setAllowedHeaders(List.of("*"));
        // Allows any origin (any domain) to make a request.
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Specifies which HTTP methods are allowed in a cross-origin request.
        // The basic ones are allowed: GET, POST, PUT, DELETE, and also OPTIONS (important for pre-flight requests).
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
