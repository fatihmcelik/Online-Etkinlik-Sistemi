package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    // UserService'i içeri alıyoruz (Dependency Injection)
    public UserController(UserService userService) {
        this.userService = userService;
    }



    // Kayıt sayfasını gösterir
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html dosyasını arar
    }

    // Kayıt formundan gelen veriyi işler
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.saveUser(user); // Veriyi servis aracılığıyla kaydeder
        return "redirect:/login?success"; // Başarıyla kaydedince logine yönlendirir
    }
}