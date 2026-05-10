package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Ticket;
import com.etkinlik.online_etkinlik.model.TicketType;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.model.enums.TicketStatus;
import com.etkinlik.online_etkinlik.repository.TicketRepository;
import com.etkinlik.online_etkinlik.repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

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
        
        // YENİ: Enum kullanıldı
        ticket.setStatus(TicketStatus.ACTIVE); 

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
    
    @Transactional
    public String validateTicket(String ticketCode, Long eventId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(ticketCode);
        
        if (ticketOpt.isEmpty()) {
            return "Geçersiz bilet kodu!";
        }
        
        Ticket ticket = ticketOpt.get();
        
        if (!ticket.getEvent().getId().equals(eventId)) {
            return "Bu bilet bu etkinlik için geçerli değil!";
        }
        
        // YENİ: Enum karşılaştırması yapıldı
        if (ticket.getStatus() == TicketStatus.USED) {
            return "Bu bilet zaten kullanılmış!";
        }
        
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return "Bu bilet iptal edilmiş!";
        }
        
        // YENİ: Enum ataması yapıldı
        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);
        
        return "Bilet başarılı bir şekilde doğrulandı. Giriş onaylandı.";
    }
}