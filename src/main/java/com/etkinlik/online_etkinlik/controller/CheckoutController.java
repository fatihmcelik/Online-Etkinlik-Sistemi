package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.service.EventService;
import com.etkinlik.online_etkinlik.repository.TicketTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    private final EventService eventService;
    private final TicketTypeRepository ticketTypeRepository; // YENİ: Bilet tiplerini çekmek için eklendi

    public CheckoutController(EventService eventService, TicketTypeRepository ticketTypeRepository) {
        this.eventService = eventService;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        // YENİ: Kullanıcının seçmesi için etkinliğe ait bilet tiplerini gönderiyoruz
        model.addAttribute("ticketTypes", ticketTypeRepository.findByEventId(eventId));
        return "checkout";
    }
}