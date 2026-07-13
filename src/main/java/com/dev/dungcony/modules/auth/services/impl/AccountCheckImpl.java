package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.entities.Account;
import com.dev.dungcony.modules.auth.exceptions.EmailIsAlredy;
import com.dev.dungcony.modules.auth.exceptions.IncorrectInput;
import com.dev.dungcony.modules.auth.exceptions.TokenExpire;
import com.dev.dungcony.modules.auth.exceptions.UserNameIsAlredy;
import com.dev.dungcony.modules.auth.repositories.AccountRepository;
import com.dev.dungcony.modules.auth.services.interfaces.AccountCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountCheckImpl implements AccountCheckService {
    private final AccountRepository accRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void existsByEmail(String email) {
        Account acc = accRepo.findByEmail(email).orElse(null);
        if (acc != null) {
            if (!acc.getVerify())
                accRepo.delete(acc);
            else
                throw new EmailIsAlredy();
        }
    }

    @Override
    public void existsByUsername(String username) {
        Account acc = accRepo.findByUsername(username).orElse(null);
        if (acc != null) {
            if (!acc.getVerify())
                accRepo.delete(acc);
            else
                throw new UserNameIsAlredy();
        }
    }

    @Override
    public void emailAndUsernameIsTrue(int accId, String email, String username) {
        Account acc = accRepo.findById(accId).orElse(null);

        if (acc == null || !acc.getUsername().equals(username) || !acc.getEmail().equals(email))
            throw new IncorrectInput();
    }

    @Override
    public void checkPassword(int accId, String password) {
        Account acc = accRepo.findById(accId).orElseThrow(TokenExpire::new);
        if (!passwordEncoder.matches(password, acc.getPassword()))
            throw new IncorrectInput();
    }
}
