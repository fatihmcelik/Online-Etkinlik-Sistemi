package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Event;
import com.etkinlik.online_etkinlik.model.TicketType;
import com.etkinlik.online_etkinlik.repository.TicketTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventService eventService;

    public TicketTypeService(TicketTypeRepository ticketTypeRepository, EventService eventService) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.eventService = eventService;
    }

    public List<TicketType> getByEventId(Long eventId) {
        return ticketTypeRepository.findByEventId(eventId);
    }

    @Transactional
    public void save(Long eventId, TicketType ticketType) {
        Event event = eventService.getEventById(eventId);
        ticketType.setEvent(event);
        
        // Yeni ekleniyorsa satılan bilet sayısını sıfırla
        if (ticketType.getId() == null) {
            ticketType.setSoldCount(0);
        }
        ticketTypeRepository.save(ticketType);
    }

    @Transactional
    public void delete(Long id) {
        TicketType tt = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bilet tipi bulunamadı"));
                
        // Eğer bu bilet tipinden daha önce satın alan olduysa silinmesini engelliyoruz (Sistemi korumak için)
        if (tt.getSoldCount() != null && tt.getSoldCount() > 0) {
            throw new RuntimeException("Bu bilet tipinden satış yapıldığı için silinemez! Sadece kotasını sıfırlayabilirsiniz.");
        }
        ticketTypeRepository.deleteById(id);
    }
}