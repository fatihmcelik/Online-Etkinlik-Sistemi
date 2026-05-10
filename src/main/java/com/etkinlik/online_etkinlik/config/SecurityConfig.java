package com.etkinlik.online_etkinlik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity 
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/events", 
                    "/events/**", // Ana sayfa ve tüm etkinlik detayları herkese açık
                    "/login", 
                    "/register", 
                    "/forgot-password/**", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/static/**", 
                    "/api/auth/**", 
                    "/api/events/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") 
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/events", true) // Giriş yapınca ana sayfaya git
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/events") // Çıkış yapınca ana sayfaya dön
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("cokGizliBirAnahtarOnlineEtkinlik")
                .tokenValiditySeconds(7 * 24 * 60 * 60)
            );

        return http.build();
    }
}