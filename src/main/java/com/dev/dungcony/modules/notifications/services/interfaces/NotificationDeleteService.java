package com.dev.dungcony.modules.notifications.services.interfaces;

import java.util.List;
import java.util.UUID;

public interface NotificationDeleteService {
    void deleteByCode(UUID uid, String code);

    int deleteListByCode(UUID uid, List<String> codes);

    int clear(UUID uid);

    void adminDeleteByCode(String code);

    int adminDeleteListByCode(List<String> codes);

    int adminClear();


}
