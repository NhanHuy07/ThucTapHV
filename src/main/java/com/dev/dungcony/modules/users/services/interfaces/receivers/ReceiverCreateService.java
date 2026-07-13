package com.dev.dungcony.modules.users.services.interfaces.receivers;

import com.dev.dungcony.modules.users.dto.req.ReceiverCreateReq;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;

import java.util.UUID;

public interface ReceiverCreateService {
    ReceiverRes create(UUID userId, ReceiverCreateReq req);
}
