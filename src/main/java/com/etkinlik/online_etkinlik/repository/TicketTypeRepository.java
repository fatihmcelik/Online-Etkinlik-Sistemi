package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
}