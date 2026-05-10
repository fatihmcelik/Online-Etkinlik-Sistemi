package com.etkinlik.online_etkinlik.controller.api;

import com.etkinlik.online_etkinlik.dto.EventDto;
import com.etkinlik.online_etkinlik.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;

    public EventApiController(EventService eventService) {
        this.eventService = eventService;
    }

    // Dışarıya sadece güvenli olan DTO objelerini JSON formatında döner
    @GetMapping
    public ResponseEntity<List<EventDto>> getActiveEvents() {
        List<EventDto> eventDtos = eventService.getActiveEvents()
                .stream()
                .map(EventDto::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(eventDtos);
    }
}