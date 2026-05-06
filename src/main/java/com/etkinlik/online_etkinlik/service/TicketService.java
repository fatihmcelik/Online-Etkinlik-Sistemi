package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Ticket;
import com.etkinlik.online_etkinlik.model.TicketType;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.TicketRepository;
import com.etkinlik.online_etkinlik.repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Transactional
    public Ticket buyTicket(User user, Long ticketTypeId) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("Bilet türü bulunamadı!"));

        if (ticketType.getQuota() <= 0) {
            throw new RuntimeException("Üzgünüz, bu bilet türü için kapasite dolmuştur.");
        }

        ticketType.setQuota(ticketType.getQuota() - 1);
        
        
        ticketType.setSoldCount(ticketType.getSoldCount() + 1); 
        
        ticketTypeRepository.save(ticketType);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTicketType(ticketType);
        ticket.setEvent(ticketType.getEvent());
        ticket.setTicketCode("ETK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setStatus("ACTIVE"); 

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}