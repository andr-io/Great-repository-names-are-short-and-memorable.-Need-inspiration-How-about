package com.andreyprodromov.java.medical.config;

import com.andreyprodromov.java.medical.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final UserService userService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests
                        (
                                authz -> authz
                                        .requestMatchers("/manage-user").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/delete-user/**").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/doctors").hasAnyAuthority("ROLE_DOCTOR", "ROLE_PATIENT", "ROLE_ADMIN")
                                        .requestMatchers("/edit-doctor").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/delete-doctor/**").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/patients").hasAnyAuthority("ROLE_DOCTOR", "ROLE_ADMIN")
                                        .requestMatchers("/edit-patient").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/delete-patient/**").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/exams").hasAnyAuthority("ROLE_DOCTOR", "ROLE_ADMIN", "ROLE_PATIENT")
                                        .requestMatchers("/edit-patient").hasAnyAuthority("ROLE_ADMIN", "ROLE_DOCTOR")
                                        .requestMatchers("/delete-patient/**").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DOCTOR")
                                        .requestMatchers( "/css/**", "/", "/index.html").permitAll()
                                        .anyRequest().authenticated()
                        ).formLogin(Customizer. withDefaults())
            .logout((logout) -> logout.logoutSuccessUrl("/"));
        return http.build();
    }
}
