package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Role;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.RoleRepository;
import com.etkinlik.online_etkinlik.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi ile zaten bir hesap var.");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Varsayılan kullanıcı rolü bulunamadı!"));
        user.getRoles().add(userRole);

        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setFailedLoginAttempts(0);
        user.setRememberMe(false);

        return userRepository.save(user);
    }

    @Transactional
    public void incrementFailedAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int currentAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(currentAttempts);
            if (currentAttempts >= 5) {
                user.setActive(false); // Hesap kilitlenir
            }
            userRepository.save(user);
        });
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    // --- YENİ EKLENEN METOTLAR ---

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void unlockUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.setActive(true);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserActiveStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserProfile(String currentUsername, String firstName, String lastName, String email) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Eğer e-posta değiştiyse ve yeni e-posta başkasına aitse hata fırlat
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Bu e-posta adresi başka bir hesap tarafından kullanılıyor.");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }
}