package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.model.Category;
import com.etkinlik.online_etkinlik.model.Event;
import com.etkinlik.online_etkinlik.model.Location;
import com.etkinlik.online_etkinlik.model.Payment;
import com.etkinlik.online_etkinlik.model.SystemLog;
import com.etkinlik.online_etkinlik.model.TicketType;
import com.etkinlik.online_etkinlik.model.enums.PaymentStatus;
import com.etkinlik.online_etkinlik.repository.PaymentRepository;
import com.etkinlik.online_etkinlik.repository.SystemLogRepository;
import com.etkinlik.online_etkinlik.repository.TicketRepository;
import com.etkinlik.online_etkinlik.service.CategoryService;
import com.etkinlik.online_etkinlik.service.EventService;
import com.etkinlik.online_etkinlik.service.LocationService;
import com.etkinlik.online_etkinlik.service.UserService;
import com.etkinlik.online_etkinlik.service.SystemLogService;
import com.etkinlik.online_etkinlik.service.TicketTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final UserService userService;
    private final SystemLogService systemLogService;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final SystemLogRepository systemLogRepository;
    private final TicketTypeService ticketTypeService;

    public AdminController(EventService eventService,
                           CategoryService categoryService,
                           LocationService locationService,
                           UserService userService,
                           SystemLogService systemLogService,
                           TicketRepository ticketRepository,
                           PaymentRepository paymentRepository,
                           SystemLogRepository systemLogRepository,
                           TicketTypeService ticketTypeService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.userService = userService;
        this.systemLogService = systemLogService;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.systemLogRepository = systemLogRepository;
        this.ticketTypeService = ticketTypeService;
    }

    @GetMapping
    public String redirectDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("totalTickets", ticketRepository.count());
        
        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalRevenue", totalRevenue);

        List<SystemLog> logs = systemLogRepository.findAll();
        logs.sort((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp())); 
        model.addAttribute("recentLogs", logs.stream().limit(6).toList()); 

        return "admin/dashboard";
    }

    @GetMapping("/logs")
    public String viewLogs(Model model) {
        List<SystemLog> logs = systemLogRepository.findAll();
        logs.sort((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()));
        model.addAttribute("logs", logs);
        return "admin/logs";
    }

    // --- ETKİNLİK İŞLEMLERİ ---

    @GetMapping("/event/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("locations", locationService.getAll());
        return "admin/event-form";
    }

    @GetMapping("/event/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("locations", locationService.getAll());
        return "admin/event-form";
    }

    // KRİTİK DÜZELTME: Düzenleme işlemlerinde veritabanı kısıtlamalarına takılmayı önleyen güncelleme
    @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute("event") Event event,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        try {
            var currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (event.getId() != null) {
                // 1. DÜZENLEME (UPDATE) MANTIĞI
                Event existingEvent = eventService.getEventById(event.getId());
                existingEvent.setTitle(event.getTitle());
                existingEvent.setDescription(event.getDescription());
                existingEvent.setStartDate(event.getStartDate());
                existingEvent.setEndDate(event.getEndDate());
                existingEvent.setActive(event.isActive());
                existingEvent.setImageUrl(event.getImageUrl());
                
                // İlişkili nesneleri veritabanından çekerek güvenli bir şekilde bağlıyoruz
                existingEvent.setCategory(categoryService.getById(event.getCategory().getId()));
                existingEvent.setLocation(locationService.getById(event.getLocation().getId()));
                
                eventService.saveEvent(existingEvent);
                systemLogService.log("EVENT_UPDATED", "Etkinlik güncellendi: " + existingEvent.getTitle(), "EVENT", existingEvent.getId(), currentUser);
            } else {
                // 2. YENİ EKLEME (CREATE) MANTIĞI
                event.setCreatedBy(currentUser);
                event.setCategory(categoryService.getById(event.getCategory().getId()));
                event.setLocation(locationService.getById(event.getLocation().getId()));
                
                eventService.saveEvent(event);
                systemLogService.log("EVENT_SAVED", "Etkinlik eklendi: " + event.getTitle(), "EVENT", event.getId(), currentUser);
            }
            
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Etkinlik kaydedilirken bir hata oluştu: " + e.getMessage());
            return "redirect:/admin/dashboard"; 
        }
    }

    @PostMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("EVENT_DELETED", "Etkinlik silindi ID: " + id, "EVENT", id, currentUser);
        eventService.deleteEvent(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/event/toggle/{id}")
    public String toggleEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var currentUser = userService.findByUsername(userDetails.getUsername());
        eventService.toggleEventStatus(id);
        systemLogService.log("EVENT_TOGGLED", "Etkinlik durumu değiştirildi ID: " + id, "EVENT", id, currentUser);
        return "redirect:/admin/dashboard";
    }

    // --- KULLANICI YÖNETİMİ İŞLEMLERİ ---

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users"; 
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.toggleUserActiveStatus(id);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("USER_TOGGLED", "Kullanıcı erişimi değiştirildi ID: " + id, "USER", id, currentUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/unlock/{id}")
    public String unlockUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.unlockUserById(id);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("USER_UNLOCKED", "Kullanıcı kilidi açıldı ID: " + id, "USER", id, currentUser);
        return "redirect:/admin/users";
    }

    // --- KATEGORİ YÖNETİMİ ---

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute Category category, @AuthenticationPrincipal UserDetails userDetails) {
        categoryService.save(category);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("CATEGORY_SAVED", "Kategori eklendi/güncellendi: " + category.getName(), "CATEGORY", category.getId(), currentUser);
        return "redirect:/admin/categories";
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        categoryService.delete(id);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("CATEGORY_DELETED", "Kategori silindi ID: " + id, "CATEGORY", id, currentUser);
        return "redirect:/admin/categories";
    }

    // --- MEKAN YÖNETİMİ ---

    @GetMapping("/locations")
    public String listLocations(Model model) {
        model.addAttribute("locations", locationService.getAll());
        model.addAttribute("location", new Location());
        return "admin/locations";
    }

    @PostMapping("/location/save")
    public String saveLocation(@ModelAttribute Location location, @AuthenticationPrincipal UserDetails userDetails) {
        locationService.save(location);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("LOCATION_SAVED", "Mekan eklendi/güncellendi: " + location.getName(), "LOCATION", location.getId(), currentUser);
        return "redirect:/admin/locations";
    }

    @PostMapping("/location/delete/{id}")
    public String deleteLocation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        locationService.delete(id);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("LOCATION_DELETED", "Mekan silindi ID: " + id, "LOCATION", id, currentUser);
        return "redirect:/admin/locations";
    }

    // --- YENİ EKLENEN: BİLET TİPİ (FİYAT/KOTA) YÖNETİMİ ---

    @GetMapping("/event/{eventId}/ticket-types")
    public String listTicketTypes(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        model.addAttribute("ticketTypes", ticketTypeService.getByEventId(eventId));
        model.addAttribute("newTicketType", new TicketType());
        return "admin/ticket-types";
    }

    @PostMapping("/event/{eventId}/ticket-types/save")
    public String saveTicketType(@PathVariable Long eventId, @ModelAttribute TicketType ticketType, @AuthenticationPrincipal UserDetails userDetails) {
        ticketTypeService.save(eventId, ticketType);
        var currentUser = userService.findByUsername(userDetails.getUsername());
        systemLogService.log("TICKET_TYPE_SAVED", "Bilet tipi eklendi/güncellendi: " + ticketType.getName(), "TICKET_TYPE", ticketType.getId(), currentUser);
        return "redirect:/admin/event/" + eventId + "/ticket-types";
    }

    @PostMapping("/ticket-types/delete/{id}")
    public String deleteTicketType(@PathVariable Long id, @RequestParam Long eventId, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            ticketTypeService.delete(id);
            var currentUser = userService.findByUsername(userDetails.getUsername());
            systemLogService.log("TICKET_TYPE_DELETED", "Bilet tipi silindi ID: " + id, "TICKET_TYPE", id, currentUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage()); 
        }
        return "redirect:/admin/event/" + eventId + "/ticket-types";
    }
}