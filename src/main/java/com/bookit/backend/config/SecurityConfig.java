package com.bookit.backend.config;

import com.bookit.backend.security.AuthEntryPoint;
import com.bookit.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private  AuthEntryPoint authEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;



    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Authentication
                        .requestMatchers("/api/v2/auth/**")
                        .permitAll()

                        // Public read APIs
                        .requestMatchers(HttpMethod.GET, "/api/v2/movies/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v2/theatres/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v2/screens/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v2/seats/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v2/shows/**")
                        .permitAll()

                        // Admin movie operations
                        .requestMatchers("/api/v2/movies/**")
                        .hasRole("ADMIN")

                        // Admin theatre operations
                        .requestMatchers("/api/v2/theatres/**")
                        .hasRole("ADMIN")

                        // Admin screen operations
                        .requestMatchers("/api/v2/screens/**")
                        .hasRole("ADMIN")

                        // Admin seat operations
                        .requestMatchers("/api/v2/seats/**")
                        .hasRole("ADMIN")

                        // Admin show operations
                        .requestMatchers("/api/v2/shows/**")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}