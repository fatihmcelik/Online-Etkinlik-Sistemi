package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByEventId(Long eventId);
}