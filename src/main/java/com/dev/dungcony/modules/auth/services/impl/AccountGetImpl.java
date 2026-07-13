package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.dtos.AccDto;
import com.dev.dungcony.modules.auth.mappers.AccountToDto;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.auth.dtos.res.AccountRes;
import com.dev.dungcony.modules.auth.entities.Account;
import com.dev.dungcony.modules.auth.exceptions.AccountNotFound;
import com.dev.dungcony.modules.auth.mappers.AccountToAccRes;
import com.dev.dungcony.modules.auth.repositories.AccountRepository;
import com.dev.dungcony.modules.auth.services.interfaces.AccountGetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountGetImpl implements AccountGetService {

    private final AccountRepository accRepo;

    @Override
    public AccountRes getProfileById(int id) {
        Account acc = accRepo.findById(id)
                .orElseThrow(AccountNotFound::new);

        return AccountToAccRes.map(acc);
    }

    @Override
    public AccDto getByUsername(String username) {
        Account acc = accRepo.findByUsername(username)
                .orElseThrow(AccountNotFound::new);

        return AccountToDto.map(acc);
    }

}
