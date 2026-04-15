package com.etkinlik.online_etkinlik.service;

import com.iyzipay.model.Payment;
import com.iyzipay.Options;
import com.iyzipay.request.CreatePaymentRequest;
import com.iyzipay.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import com.etkinlik.online_etkinlik.model.User;

@Service
public class PaymentService {

    @Value("${iyzico.api.key}")
    private String apiKey;

    @Value("${iyzico.secret.key}")
    private String secretKey;

    @Value("${iyzico.base.url}")
    private String baseUrl;

    private Options options;

    @PostConstruct
    public void init() {
        options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
    }

    @Autowired
    private TicketService ticketService;

    public String processPayment(User user, Long ticketTypeId, CreatePaymentRequest request) {
        // 1. Iyzico'ya ödeme isteğini gönder
        Payment payment = Payment.create(request, options);

        // 2. Arka Plan Bağlantı Kontrolü
        if (payment.getStatus().equals(Status.SUCCESS.getValue())) {
            // Ödeme başarılıysa 4. haftada yazdığın bilet üretimini tetikle
            ticketService.buyTicket(user, ticketTypeId);
            return "Ödeme başarılı, biletiniz oluşturuldu.";
        } else {
            // Ödeme başarısızsa hata mesajını dön
            return "Ödeme hatası: " + payment.getErrorMessage();
        }
    }
}