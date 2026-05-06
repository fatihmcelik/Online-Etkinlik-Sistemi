package com.etkinlik.online_etkinlik.controller;

import com.etkinlik.online_etkinlik.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private final UserService userService;

    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    
}