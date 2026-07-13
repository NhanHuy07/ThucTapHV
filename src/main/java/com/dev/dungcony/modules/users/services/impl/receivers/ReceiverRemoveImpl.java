package com.dev.dungcony.modules.users.services.impl.receivers;

import com.dev.dungcony.modules.users.entities.Receiver;
import com.dev.dungcony.modules.users.exceptions.ReceiverIdConflict;
import com.dev.dungcony.modules.users.exceptions.ReceiverNotFound;
import com.dev.dungcony.modules.users.repositories.RecieverRepository;
import com.dev.dungcony.modules.users.services.interfaces.receivers.ReceiverRemoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReceiverRemoveImpl implements ReceiverRemoveService {

    private final RecieverRepository recieverRepository;

    @Override
    public void removeById(int rId) {
        recieverRepository.deleteById(rId);
    }

    @Override
    public void removeReceiverUserById(UUID uId, int rId) {
        Receiver r = recieverRepository.findById(rId)
                .orElseThrow(ReceiverNotFound::new);

        if (!r.getUser().getId().equals(uId))
            throw new ReceiverIdConflict();

        recieverRepository.delete(r);
    }

    @Transactional
    @Override
    public void removeAllByUser(UUID uId) {
        recieverRepository.deleteAllByUserId(uId);
    }

    @Override
    public void removeAll() {
        recieverRepository.deleteAll();
    }
}
