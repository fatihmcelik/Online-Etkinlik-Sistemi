package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.PasswordResetToken;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.PasswordResetTokenRepository;
import com.etkinlik.online_etkinlik.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // YENİ EKLENEN METOT: Token'ı veritabanına yazmadan önce SHA-256 ile şifreler (Hashler)
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Şifreleme algoritması bulunamadı", e);
        }
    }

    @Transactional
    public String createResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu email ile kayıtlı kullanıcı bulunamadı."));

        tokenRepository.deleteByUserId(user.getId());

        // Kullanıcıya gönderilecek ham token
        String rawToken = UUID.randomUUID().toString();
        
        // Veritabanına kaydedilecek şifrelenmiş (hashlenmiş) token
        String hashedToken = hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(hashedToken);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // Mail sistemine ham token gönderilir (Veritabanındaki hashlenmiş olanla eşleşemez)
        return rawToken;
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        // Linkten gelen ham token'ı hashleyip veritabanında öyle arıyoruz
        String hashedToken = hashToken(rawToken);
        
        PasswordResetToken resetToken = tokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş token."));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Bu şifre sıfırlama bağlantısı daha önce kullanılmış.");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Şifre sıfırlama bağlantısının süresi dolmuş.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}