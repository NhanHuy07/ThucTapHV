package com.dev.dungcony.modules.notifications.services.interfaces;

import java.util.UUID;

public interface NotificationUpdateService {
    void userReaded(String code, UUID uid);

    long userReadAll(UUID uid);

    void adminReaded(String code);

    long adminReadAll();
}
