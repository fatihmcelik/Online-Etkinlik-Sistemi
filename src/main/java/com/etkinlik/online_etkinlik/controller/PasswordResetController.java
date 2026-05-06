package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/forgot-password")
public class PasswordResetController {
    private final PasswordResetService resetService;

    public PasswordResetController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    @GetMapping
    public String showForgotPasswordForm() {
        return "forgot-password"; // forgot-password.html
    }

    @PostMapping
    public String processForgotPassword(@RequestParam("email") String email) {
        try {
            String token = resetService.createResetToken(email);
            // Gerçek projede burada e-posta gönderilir. Ödev için token'ı loglayabilirsin.
            System.out.println("Şifre sıfırlama linki: /forgot-password/reset?token=" + token);
            return "redirect:/login?resetSent";
        } catch (Exception e) {
            return "redirect:/forgot-password?error";
        }
    }

    @GetMapping("/reset")
    public String showResetForm(@RequestParam("token") String token) {
        return "reset-password"; // reset-password.html
    }

    @PostMapping("/reset")
    public String processReset(@RequestParam("token") String token, @RequestParam("password") String password) {
        try {
            resetService.resetPassword(token, password);
            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            return "redirect:/forgot-password/reset?token=" + token + "&error";
        }
    }
}