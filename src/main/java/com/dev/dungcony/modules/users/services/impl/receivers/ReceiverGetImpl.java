package com.dev.dungcony.modules.users.services.impl.receivers;

import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.entities.Receiver;
import com.dev.dungcony.modules.users.entities.User;
import com.dev.dungcony.modules.users.exceptions.ReceiverIdConflict;
import com.dev.dungcony.modules.users.exceptions.ReceiverNotFound;
import com.dev.dungcony.modules.users.mappers.ReceiverMapper;
import com.dev.dungcony.modules.users.repositories.RecieverRepository;
import com.dev.dungcony.modules.users.services.interfaces.receivers.RecieverGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReceiverGetImpl implements RecieverGetService {

    private final RecieverRepository recieverRepository;

    @Override
    public ReceiverRes getReceiverById(UUID userId, Integer id) {
        return ReceiverMapper.toRes(findById(userId, id));
    }

    @Override
    public ReceiverRes adminGetReceiverById(Integer id) {
        Receiver re = recieverRepository.findById(id)
                .orElseThrow(ReceiverNotFound::new);

        return ReceiverMapper.toRes(re);
    }

    @Override
    public Page<ReceiverRes> adminGetAll(Pageable pageable) {
        return recieverRepository.findAll(pageable)
                .map(ReceiverMapper::toRes);
    }

    @Override
    public List<ReceiverRes> getAllByUser(UUID userId) {
        log.info("lấy toàn bộ receiver ....");
        User u = new User();
        u.setId(userId);

        List<Receiver> resul = recieverRepository.findAllByUser(u);

        return resul.stream()
                .map(ReceiverMapper::toRes)
                .toList();
    }

    // -----PRIVATE-----//
    private Receiver findById(UUID userId, int id) {
        Receiver re = recieverRepository.findById(id)
                .orElseThrow(ReceiverNotFound::new);

        if (!re.getUser().getId().equals(userId))
            throw new ReceiverIdConflict();

        return re;
    }
}
