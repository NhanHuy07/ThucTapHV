package com.dev.dungcony.modules.auth.services.interfaces;

public interface AccountCreateService {
    void createAccount(String email, String username, String password);

    String createAdminAccount(String email, String password);
}
