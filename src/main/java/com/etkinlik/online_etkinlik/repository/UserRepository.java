package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Email ile kullanıcı bul — login için
    Optional<User> findByEmail(String email);
    
    // Username ile kullanıcı bul — Spring Security için
    Optional<User> findByUsername(String username);
    
    // Email var mı kontrol — kayıt için
    boolean existsByEmail(String email);
    
    // Username var mı kontrol — kayıt için
    boolean existsByUsername(String username);
}