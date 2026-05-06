package com.etkinlik.online_etkinlik.config;

import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.service.UserService;
import com.etkinlik.online_etkinlik.service.SystemLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService userService;
    private final SystemLogService logService;

    public LoginSuccessListener(UserService userService, SystemLogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        userService.resetFailedAttempts(userDetails.getUsername());
        
        
        User user = userService.findByUsername(userDetails.getUsername());
        logService.log("USER_LOGIN_SUCCESS", "Başarılı giriş: " + userDetails.getUsername(), "USER", user.getId(), user);
    }
}