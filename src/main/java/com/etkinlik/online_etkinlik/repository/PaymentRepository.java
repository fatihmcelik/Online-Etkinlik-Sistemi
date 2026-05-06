package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByIyzicoPaymentId(String iyzicoPaymentId);
}