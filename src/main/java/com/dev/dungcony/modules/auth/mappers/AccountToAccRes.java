package com.dev.dungcony.modules.auth.mappers;

import com.dev.dungcony.modules.auth.dtos.res.AccountRes;
import com.dev.dungcony.modules.auth.entities.Account;

public class AccountToAccRes {
    public static AccountRes map(Account acc) {
        return new AccountRes(
                acc.getEmail(),
                acc.getUsername(),
                acc.getVerify(),
                acc.getRole(),
                acc.getStatus()
        );
    }
}
