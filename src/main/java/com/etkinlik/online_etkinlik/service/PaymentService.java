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
import com.etkinlik.online_etkinlik.model.Ticket;
import com.etkinlik.online_etkinlik.repository.PaymentRepository;

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

    @Autowired
    private PaymentRepository paymentRepository;

    public String processPayment(User user, Long ticketTypeId, CreatePaymentRequest request) {
        Payment iyzicoPayment = Payment.create(request, options);

        if (iyzicoPayment.getStatus().equals(Status.SUCCESS.getValue())) {
            
            
            Ticket generatedTicket = ticketService.buyTicket(user, ticketTypeId);
            
            
            com.etkinlik.online_etkinlik.model.Payment myPayment = new com.etkinlik.online_etkinlik.model.Payment();
            myPayment.setAmount(request.getPrice());
            myPayment.setIyzicoPaymentId(iyzicoPayment.getPaymentId());
            myPayment.setStatus("BASARILI");
            myPayment.setUser(user);
            
            
            myPayment.setTicket(generatedTicket);
            
            paymentRepository.save(myPayment);

            return "Ödeme başarılı, biletiniz oluşturuldu.";
        } else {
            return "Ödeme hatası: " + iyzicoPayment.getErrorMessage();
        }
    }
}