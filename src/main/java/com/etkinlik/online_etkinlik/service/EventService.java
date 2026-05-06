package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Event;
import com.etkinlik.online_etkinlik.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Etkinlik bulunamadı."));
    }

    @Transactional
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    
    
    @Transactional
    public void toggleEventStatus(Long id) {
        Event event = getEventById(id);
        event.setActive(!event.isActive());
        eventRepository.save(event);
    }
}