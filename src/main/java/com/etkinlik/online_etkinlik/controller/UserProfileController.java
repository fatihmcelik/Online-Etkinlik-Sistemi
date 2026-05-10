package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.FavoriteRepository;
import com.etkinlik.online_etkinlik.repository.ReviewRepository;
import com.etkinlik.online_etkinlik.service.TicketService;
import com.etkinlik.online_etkinlik.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserProfileController {

    private final UserService userService;
    private final TicketService ticketService;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    public UserProfileController(UserService userService, 
                                 TicketService ticketService, 
                                 FavoriteRepository favoriteRepository, 
                                 ReviewRepository reviewRepository) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        
        // YENİ: Kullanıcının favorileri ve yorumları sayfaya gönderiliyor
        model.addAttribute("favorites", favoriteRepository.findByUserId(user.getId()));
        model.addAttribute("reviews", reviewRepository.findByUserId(user.getId()));
        
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(userDetails.getUsername(), firstName, lastName, email);
            redirectAttributes.addFlashAttribute("success", "Profil bilgileriniz başarıyla güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("tickets", ticketService.getUserTickets(user.getId()));
        return "tickets";
    }
}