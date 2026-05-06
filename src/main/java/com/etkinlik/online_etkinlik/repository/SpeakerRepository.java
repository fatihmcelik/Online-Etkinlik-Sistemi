package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, Long> {
}