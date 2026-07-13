package com.dev.dungcony.modules.users.services.interfaces.receivers;

import java.util.UUID;

public interface ReceiverRemoveService {
    void removeById(int rId);

    void removeReceiverUserById(UUID uId, int rId);

    void removeAllByUser(UUID uId);

    void removeAll();
}
