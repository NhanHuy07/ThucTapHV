package com.dev.dungcony.modules.auth.services.interfaces;

public interface AccountCheckService {
    void existsByEmail(String email);

    void existsByUsername(String username);

    void emailAndUsernameIsTrue(int accId, String email, String username);

    void checkPassword(int accId, String password);
}
