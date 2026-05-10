package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.service.PaymentService;
import com.etkinlik.online_etkinlik.service.UserService;
import com.etkinlik.online_etkinlik.service.SystemLogService; // YENİ EKLENDİ
import com.iyzipay.request.CreatePaymentRequest;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.PaymentCard;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SystemLogService systemLogService; // YENİ EKLENDİ

    public PaymentController(PaymentService paymentService, UserService userService, SystemLogService systemLogService) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.systemLogService = systemLogService;
    }

    @PostMapping("/pay")
    public String processPayment(@RequestParam Long eventId, 
                                 @RequestParam Long ticketTypeId,
                                 @RequestParam String cardHolderName,
                                 @RequestParam String cardNumber,
                                 @RequestParam String expireMonth,
                                 @RequestParam String expireYear,
                                 @RequestParam String cvc,
                                 @RequestParam String price, 
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(userDetails.getUsername());
        String safePrice = price.replace(",", ".");

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("ETK-" + System.currentTimeMillis());
        request.setPrice(new BigDecimal(safePrice));
        request.setPaidPrice(new BigDecimal(safePrice));
        
        request.setCurrency(Currency.TRY.name()); 
        
        request.setInstallment(1);
        request.setBasketId("B" + ticketTypeId);
        request.setPaymentChannel("WEB");
        request.setPaymentGroup("PRODUCT");

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(cardHolderName);
        paymentCard.setCardNumber(cardNumber.replaceAll("\\s+", ""));
        paymentCard.setExpireMonth(expireMonth);
        paymentCard.setExpireYear(expireYear);
        paymentCard.setCvc(cvc);
        paymentCard.setRegisterCard(0);
        request.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId(user.getId().toString());
        buyer.setName(user.getFirstName());
        buyer.setSurname(user.getLastName());
        buyer.setGsmNumber("+905320000000"); 
        buyer.setEmail(user.getEmail());
        buyer.setIdentityNumber("74300864791"); 
        buyer.setRegistrationAddress("Etkinlik Mahallesi, Online Sokak No:1");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        request.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName(user.getFirstName() + " " + user.getLastName());
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Etkinlik Mahallesi, Online Sokak No:1");
        request.setShippingAddress(shippingAddress);
        request.setBillingAddress(shippingAddress);

        List<BasketItem> basketItems = new ArrayList<>();
        BasketItem item = new BasketItem();
        item.setId("TICKET-" + ticketTypeId);
        item.setName("Etkinlik Bileti");
        item.setCategory1("E-Bilet");
        
        item.setItemType(BasketItemType.VIRTUAL.name()); 
        item.setPrice(new BigDecimal(safePrice));
        basketItems.add(item);
        request.setBasketItems(basketItems);

        try {
            String resultMessage = paymentService.processPayment(user, ticketTypeId, request);
            
            if (resultMessage.contains("hata")) {
                redirectAttributes.addFlashAttribute("errorMessage", resultMessage);
                return "redirect:/checkout?eventId=" + eventId + "&error"; 
            }
            
            redirectAttributes.addFlashAttribute("success", "Ödeme başarılı! Biletiniz oluşturuldu.");
            return "redirect:/tickets"; 
        } catch (Exception e) {
            e.printStackTrace(); 
            
            // YENİ EKLENEN: Başarısız bilet alımı, kota yetersizliği vs. anında loglanıyor!
            systemLogService.log("PAYMENT_FAILED", "Bilet Alım Hatası: " + e.getMessage(), "PAYMENT", null, user);
            
            // Kullanıcıya "Sistemsel bir hata..." demek yerine direkt "Üzgünüz kapasite doldu" diyeceğiz
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout?eventId=" + eventId + "&error";
        }
    }
}