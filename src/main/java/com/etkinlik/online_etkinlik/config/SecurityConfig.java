package com.etkinlik.online_etkinlik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }


// ... mevcut kodların ...

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Geliştirme aşamasında (Postman vb. testleri için) genellikle kapatılır
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/events/**").permitAll() // Etkinlikleri herkes görebilmeli
                .requestMatchers("/api/auth/**").permitAll()   // Kayıt ve Giriş sayfaları herkese açık
                .anyRequest().authenticated()                  // Geri kalan her işlem (Bilet alımı vb.) için giriş şart
            )
            .formLogin(form -> form
                .loginPage("/login") // Nestan'ın hazırlayacağı giriş sayfası
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}