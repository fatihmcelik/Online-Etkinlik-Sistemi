package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.UserRepository; // Fatih'in oluşturacağı Repository
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        // Profesyonel Kontrol: Şifreyi veritabanına gitmeden önce hashle
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }
}