package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // KRİTİK DÜZELTME: Spring'in tanıması için eklendi
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}