package com.etkinlik.online_etkinlik.config;

import com.etkinlik.online_etkinlik.service.UserService;
import com.etkinlik.online_etkinlik.service.SystemLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final UserService userService;
    private final SystemLogService logService;

    public LoginFailureListener(UserService userService, SystemLogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        userService.incrementFailedAttempts(username);
        
        
        logService.log("USER_LOGIN_FAILED", "Başarısız giriş denemesi: " + username, "USER", null, null);
    }
}