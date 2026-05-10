package com.etkinlik.online_etkinlik.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        // GİZLİ HATAYI KONSOLDA GÖRMEK İÇİN EKLENEN KRİTİK SATIR:
        // Bu sayede arka planda patlayan hatanın ne olduğunu (veritabanı mı, null pointer mı vb.) IDE terminalinden okuyabileceğiz.
        ex.printStackTrace();
        
        // Kullanıcıya gösterilecek hata mesajı
        redirectAttributes.addFlashAttribute("error", "İşlem sırasında bir hata oluştu: " + ex.getMessage());
        
        // Kullanıcının hata almadan hemen önce bulunduğu sayfayı (Referer) tespit etme
        String referer = request.getHeader("Referer");
        
        // Eğer kullanıcı sitede geziyorken bir hata aldıysa, onu bir önceki sayfaya geri gönder
        if (referer != null) {
            return "redirect:" + referer;
        }
        
        // Eğer referer yoksa (örneğin site adresini tarayıcıya direkt yazıp girdiyse) 
        // ve sayfa yüklenirken hata patladıysa güvenlik gereği login'e atar.
        return "redirect:/login"; 
    }
}