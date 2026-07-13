package com.dev.dungcony.modules.users.services.impl.receivers;

import com.dev.dungcony.modules.users.dto.req.ReceiverCreateReq;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.entities.Receiver;
import com.dev.dungcony.modules.users.entities.User;
import com.dev.dungcony.modules.users.mappers.ReceiverMapper;
import com.dev.dungcony.modules.users.repositories.RecieverRepository;
import com.dev.dungcony.modules.users.repositories.UserRepository;
import com.dev.dungcony.modules.users.services.interfaces.receivers.ReceiverCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReceiverCreateImpl implements ReceiverCreateService {

    private final RecieverRepository recieverRepository;
    private final UserRepository userRepository;

    @Override
    public ReceiverRes create(UUID userId, ReceiverCreateReq req) {

        User user = userRepository.getReferenceById(userId);

        Receiver receiver = ReceiverMapper.toEntity(req);
        receiver.setUser(user);

        recieverRepository.save(receiver);

        return ReceiverMapper.toRes(receiver);
    }
}
