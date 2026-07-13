package com.dev.dungcony.modules.auth.mappers;

import com.dev.dungcony.modules.auth.dtos.AccDto;
import com.dev.dungcony.modules.auth.entities.Account;

public class AccountToDto {
    public static AccDto map(Account account) {
        return new AccDto(
                account.getId(),
                account.getEmail(),
                account.getUsername(),
                account.getPassword(),
                account.getVerify(),
                account.getRole(),
                account.getStatus()
        );
    }
}
