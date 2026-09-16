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
                        // 401 Unauthorized: Triggers via AuthEntryPoint when the user has NO token/identity
                        .authenticationEntryPoint(authEntryPoint)

                        // 403 Forbidden: Triggers when the user is logged in (CUSTOMER) but lacks the required role (ADMIN)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN); // 403 Status
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {
                                        "status" : 403,
                                        "error" : "Forbidden",
                                        "message" : "Access Denied: You do not have the required administrative permissions"
                                    }
                                """);
                            })
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Authentication
                        .requestMatchers("/api/*/auth/**")
                        .permitAll()

                        // Public read APIs
                        .requestMatchers(HttpMethod.GET, "/api/*/movies", "/api/*/movies/", "/api/*/movies/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/*/theatres", "/api/*/theatres/", "/api/*/theatres/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/*/screens", "/api/*/screens/", "/api/*/screens/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/*/seats", "/api/*/seats/", "/api/*/seats/**")
                        .permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/*/shows", "/api/*/shows/", "/api/*/shows/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/*/users")
                        .hasRole("ADMIN")

                        // Admin movie operations
                        .requestMatchers("/api/*/movies/**")
                        .hasRole("ADMIN")

                        // Admin theatre operations
                        .requestMatchers("/api/*/theatres/**")
                        .hasRole("ADMIN")

                        // Admin screen operations
                        .requestMatchers("/api/*/screens/**")
                        .hasRole("ADMIN")

                        // Admin seat operations
                        .requestMatchers("/api/*/seats/**")
                        .hasRole("ADMIN")

                        // Admin show operations
                        .requestMatchers("/api/*/shows/**")
                        .hasRole("ADMIN")

                        // Admin user operations
                        .requestMatchers("/api/*/user/**")
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