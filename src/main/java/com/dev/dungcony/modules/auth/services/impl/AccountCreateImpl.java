package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.entities.Account;
import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.auth.repositories.AccountRepository;
import com.dev.dungcony.modules.auth.services.interfaces.AccountCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountCreateImpl implements AccountCreateService {

    private final AccountRepository accRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createAccount(String email, String username, String password) {

        Account account = new Account();
        account.setEmail(email);
        account.setUsername(username);
        account.setPassword(password); // password đã được encode từ caller

        accRepository.save(account);
    }

    @Override
    public String createAdminAccount(String email, String password) {
        Account account = new Account();

        account.setEmail("hi@gmail.com");
        account.setUsername(email);
        account.setPassword(passwordEncoder.encode(password)); // password đã được encode từ caller
        account.setRole(Role.ADMIN);
        account.setVerify(true);

        accRepository.save(account);

        return "OK";
    }


}
