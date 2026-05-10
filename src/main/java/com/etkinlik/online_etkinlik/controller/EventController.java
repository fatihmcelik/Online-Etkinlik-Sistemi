package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.model.Event;
import com.etkinlik.online_etkinlik.model.Favorite;
import com.etkinlik.online_etkinlik.model.Review;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.FavoriteRepository;
import com.etkinlik.online_etkinlik.repository.ReviewRepository;
import com.etkinlik.online_etkinlik.service.CategoryService;
import com.etkinlik.online_etkinlik.service.EventService;
import com.etkinlik.online_etkinlik.service.LocationService; // YENİ EKLENDİ
import com.etkinlik.online_etkinlik.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryService categoryService; 
    private final LocationService locationService; // YENİ EKLENDİ

    public EventController(EventService eventService, 
                           UserService userService, 
                           FavoriteRepository favoriteRepository, 
                           ReviewRepository reviewRepository,
                           CategoryService categoryService,
                           LocationService locationService) {
        this.eventService = eventService;
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.categoryService = categoryService;
        this.locationService = locationService;
    }

    // YENİ: Fiyat, Mekan ve Tarih parametreleri eklendi
    @GetMapping
    public String listEvents(@RequestParam(required = false) Long categoryId, 
                             @RequestParam(required = false) Long locationId,
                             @RequestParam(required = false) String eventDate,
                             @RequestParam(required = false) BigDecimal maxPrice,
                             @RequestParam(required = false) String keyword,
                             Model model) {
        
        List<Event> events = eventService.getActiveEvents();
        
        // 1. Kategori Filtresi
        if (categoryId != null) {
            events = events.stream().filter(e -> e.getCategory().getId().equals(categoryId)).toList();
        }
        
        // 2. Mekan Filtresi
        if (locationId != null) {
            events = events.stream().filter(e -> e.getLocation().getId().equals(locationId)).toList();
        }
        
        // 3. Tarih Filtresi
        if (eventDate != null && !eventDate.isEmpty()) {
            LocalDate date = LocalDate.parse(eventDate);
            events = events.stream().filter(e -> e.getStartDate().toLocalDate().equals(date)).toList();
        }
        
        // 4. Maksimum Fiyat Filtresi (Etkinliğin biletleri arasında kullanıcının bütçesine uygun bilet var mı?)
        if (maxPrice != null) {
            events = events.stream()
                .filter(e -> e.getTicketTypes() != null && e.getTicketTypes().stream()
                    .anyMatch(t -> t.getPrice() != null && t.getPrice().compareTo(maxPrice) <= 0))
                .toList();
        }
        
        // 5. Kelime Arama (Arama Çubuğu)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            events = events.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(lowerKeyword) || 
                                 (e.getLocation() != null && e.getLocation().getName().toLowerCase().contains(lowerKeyword)))
                    .toList();
            model.addAttribute("keyword", keyword); 
        }
        
        model.addAttribute("events", events);
        model.addAttribute("categories", categoryService.getAll()); 
        model.addAttribute("locations", locationService.getAll()); // Seçim için tüm mekanları gönderiyoruz
        
        List<Event> featuredEvents = eventService.getActiveEvents().stream().limit(5).toList();
        model.addAttribute("featuredEvents", featuredEvents);
        
        // Ekranda kullanıcının yaptığı seçimlerin silinmemesi (seçili kalması) için:
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedLocationId", locationId);
        model.addAttribute("selectedDate", eventDate);
        model.addAttribute("selectedPrice", maxPrice);
        
        return "main";
    }

    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("reviews", reviewRepository.findByEventId(id));

        boolean isFavorite = false;
        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            isFavorite = favoriteRepository.existsByUserIdAndEventId(user.getId(), id);
        }
        model.addAttribute("isFavorite", isFavorite);
        return "event-detail";
    }

    @PostMapping("/{id}/favorite")
    public String toggleFavorite(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        User user = userService.findByUsername(userDetails.getUsername());
        Optional<Favorite> existingFav = favoriteRepository.findByUserIdAndEventId(user.getId(), id);
        if (existingFav.isPresent()) {
            favoriteRepository.delete(existingFav.get());
            redirectAttributes.addFlashAttribute("success", "Etkinlik favorilerden çıkarıldı.");
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setEvent(eventService.getEventById(id));
            favoriteRepository.save(favorite);
            redirectAttributes.addFlashAttribute("success", "Etkinlik favorilere eklendi.");
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id, @RequestParam Integer rating, @RequestParam String comment, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        Review review = new Review();
        review.setUser(userService.findByUsername(userDetails.getUsername()));
        review.setEvent(eventService.getEventById(id));
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("success", "Değerlendirmeniz başarıyla eklendi.");
        return "redirect:/events/" + id;
    }
}