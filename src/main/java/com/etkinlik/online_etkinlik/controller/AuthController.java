
package com.etkinlik.online_etkinlik.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html'i göster
    }

 /*   @GetMapping("/register")
    public String registerPage() {
        return "register"; // register.html'i göster
    } */

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
    }
}