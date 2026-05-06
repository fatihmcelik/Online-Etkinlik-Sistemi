package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.UserRepository;
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
        // Şifreyi hashle
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        user.setCreatedAt(java.time.LocalDateTime.now()); // Kayıt tarihi
        user.setActive(true); // Kullanıcıyı aktif yap
        user.setFailedLoginAttempts(0); // Başarısız girişleri sıfırla
        user.setRememberMe(false);

        return userRepository.save(user);
    }
}