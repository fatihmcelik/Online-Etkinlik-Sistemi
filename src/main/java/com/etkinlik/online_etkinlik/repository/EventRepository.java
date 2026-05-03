package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    List<Event> findByCategoryId(Long categoryId);
}