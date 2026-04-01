package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
}