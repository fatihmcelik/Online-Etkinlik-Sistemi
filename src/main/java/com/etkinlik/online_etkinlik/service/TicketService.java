package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Ticket;
import com.etkinlik.online_etkinlik.model.TicketType;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.TicketRepository;
import com.etkinlik.online_etkinlik.repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    /**
     * Kullanıcı için bilet üretir ve bilet türünün kotasını düşürür.
     */
    @Transactional
    public Ticket buyTicket(User user, Long ticketTypeId) {
        // 1. İlgili Bilet Türünü Bul (VIP mi, Standart mı?)
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("Bilet türü bulunamadı!"));

        // 2. Kapasite Kontrolü Yap (Kapasite Düşme Mantığı)
        if (ticketType.getQuota() <= 0) {
            throw new RuntimeException("Üzgünüz, bu bilet türü için kapasite dolmuştur.");
        }

        // 3. Kapasiteyi Bir Azalt ve Güncelle
        ticketType.setQuota(ticketType.getQuota() - 1);
        ticketTypeRepository.save(ticketType);

        // 4. Yeni Bilet Oluştur ve Kaydet
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTicketType(ticketType);
        ticket.setStatus("ACTIVE"); // Başlangıç durumu

        return ticketRepository.save(ticket);
    }
}