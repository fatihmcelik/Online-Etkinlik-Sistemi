package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    
    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getActiveEvents());
        return "main";
    }

    
    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "event-detail";
    }
}