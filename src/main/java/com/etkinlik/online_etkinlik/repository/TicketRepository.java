package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByEventId(Long eventId);
    
    // YENİ: Etkinlik kapısında bilet doğrulama için eklendi
    Optional<Ticket> findByTicketCode(String ticketCode);
}