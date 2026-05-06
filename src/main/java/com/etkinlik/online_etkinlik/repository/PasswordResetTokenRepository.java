package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

   
    Optional<PasswordResetToken> findByToken(String token);

    
    void deleteByUserId(Long userId);
}