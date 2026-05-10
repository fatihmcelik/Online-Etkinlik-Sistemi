package com.etkinlik.online_etkinlik.service;

import com.iyzipay.model.Payment;
import com.iyzipay.Options;
import com.iyzipay.request.CreatePaymentRequest;
import com.iyzipay.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.model.Ticket;
import com.etkinlik.online_etkinlik.model.PaymentItem;
import com.etkinlik.online_etkinlik.model.enums.PaymentStatus;
import com.etkinlik.online_etkinlik.repository.PaymentRepository;
import com.etkinlik.online_etkinlik.repository.PaymentItemRepository;

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

    @Autowired
    private PaymentItemRepository paymentItemRepository;

    // YENİ EKLENEN: Log tutabilmek için SystemLogService'i dahil ettik
    @Autowired
    private SystemLogService systemLogService;

    @Transactional 
    public String processPayment(User user, Long ticketTypeId, CreatePaymentRequest request) {
        Payment iyzicoPayment = Payment.create(request, options);

        if (iyzicoPayment.getStatus().equals(Status.SUCCESS.getValue())) {
            
            Ticket generatedTicket = ticketService.buyTicket(user, ticketTypeId);
            
            com.etkinlik.online_etkinlik.model.Payment myPayment = new com.etkinlik.online_etkinlik.model.Payment();
            myPayment.setAmount(request.getPrice());
            myPayment.setIyzicoPaymentId(iyzicoPayment.getPaymentId());
            
            myPayment.setStatus(PaymentStatus.SUCCESS);
            
            myPayment.setUser(user);
            myPayment.setTicket(generatedTicket);
            
            myPayment = paymentRepository.save(myPayment);

            for (com.iyzipay.model.BasketItem iyziItem : request.getBasketItems()) {
                PaymentItem item = new PaymentItem();
                item.setItemId(iyziItem.getId());
                item.setItemName(iyziItem.getName());
                item.setItemPrice(iyziItem.getPrice());
                item.setItemQuantity(1);
                item.setCategory1(iyziItem.getCategory1());
                item.setPayment(myPayment);
                
                paymentItemRepository.save(item);
            }

            // YENİ EKLENEN: Başarılı ödeme işlemi loglanıyor (Hangi biletin alındığı PNR kodu ile birlikte)
            systemLogService.log("TICKET_PURCHASED", "Bilet satın alındı. PNR: " + generatedTicket.getTicketCode(), "TICKET", generatedTicket.getId(), user);

            return "Ödeme başarılı, biletiniz oluşturuldu.";
        } else {
            
            // YENİ EKLENEN: Başarısız ödeme denemeleri de güvenlik amacıyla loglanıyor
            systemLogService.log("PAYMENT_FAILED", "Ödeme başarısız: " + iyzicoPayment.getErrorMessage(), "PAYMENT", null, user);
            
            return "Ödeme hatası: " + iyzicoPayment.getErrorMessage();
        }
    }
}