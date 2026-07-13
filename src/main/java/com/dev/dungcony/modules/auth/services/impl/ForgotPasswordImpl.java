package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.helpers.Generate;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.auth.services.interfaces.AccountUpdateService;
import com.dev.dungcony.modules.auth.services.interfaces.EmailService;
import com.dev.dungcony.modules.auth.services.interfaces.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ForgotPasswordImpl implements ForgotPasswordService {
    private final AccountUpdateService accountUpdateService;
    private final EmailService emailService;
    private final Generate generate;

    public void forgotPassword(String email) {
        String newPassword = generate.password(12);
        accountUpdateService.updatePassword(email, newPassword);
        emailService.sendNewPassword(email, newPassword);
    }
}
