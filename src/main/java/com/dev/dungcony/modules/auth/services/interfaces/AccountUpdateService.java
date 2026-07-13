package com.dev.dungcony.modules.auth.services.interfaces;

import com.dev.dungcony.modules.auth.enums.Status;

public interface AccountUpdateService {
    void updatePassword(int accId, String oldPassword, String newPassword);

    void updatePassword(String email, String newPassword);

    void updateEmail(int accId, String newEmail);

    void verify(String email);

    void updateVerify(int accId, boolean isVerify);

    void updateStatus(int accId, Status newStatus);
}
