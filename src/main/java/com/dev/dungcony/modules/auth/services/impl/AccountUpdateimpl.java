package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.entities.Account;
import com.dev.dungcony.modules.auth.enums.Status;
import com.dev.dungcony.modules.auth.exceptions.IncorrectInput;
import com.dev.dungcony.modules.auth.exceptions.TokenInvalid;
import com.dev.dungcony.modules.auth.repositories.AccountRepository;
import com.dev.dungcony.modules.auth.services.interfaces.AccountUpdateService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class AccountUpdateimpl implements AccountUpdateService {
    private final AccountRepository accRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updatePassword(int accId, String oldPassword, String newPassword) {
        Account acc = accRepo.findById(accId).orElseThrow(TokenInvalid::new);

        if (!passwordEncoder.matches(oldPassword, acc.getPassword()))
            throw new IncorrectInput();

        acc.setPassword(passwordEncoder.encode(newPassword));
        accRepo.save(acc);
        log.info("Password updated for account: {}", acc.getUsername());
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        Account acc = accRepo.findByEmail(email).orElseThrow(TokenInvalid::new);
        acc.setPassword(passwordEncoder.encode(newPassword));
        accRepo.save(acc);
        log.info("Password updated for account: {}", acc.getUsername());
    }

    @Override
    public void updateEmail(int accId, String newEmail) {
        Account acc = accRepo.findById(accId).orElseThrow(TokenInvalid::new);
        acc.setEmail(newEmail);
        accRepo.save(acc);
        log.info("Email updated for account: {}", acc.getUsername());
    }

    @Override
    public void verify(String email) {
        int cntRows = accRepo.verifyEmail(email);
        if (cntRows == 0)
            throw new IllegalArgumentException("Email not found");
    }

    @Override
    public void updateVerify(int accId, boolean isVerify) {
        int cntRows = accRepo.updateVerify(accId, isVerify);
        if (cntRows == 0)
            throw new IllegalArgumentException("Email not found");
    }

    @Override
    public void updateStatus(int accId, Status newStatus) {
        Account acc = accRepo.findById(accId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accId));
        acc.setStatus(newStatus);
        accRepo.save(acc);
        log.info("Status updated to {} for account: {}", newStatus, acc.getUsername());
    }
}
