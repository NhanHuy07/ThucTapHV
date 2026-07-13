package com.dev.dungcony.modules.notifications.services.impl;

import com.dev.dungcony.modules.notifications.entities.Notification;
import com.dev.dungcony.modules.notifications.exceptions.NotiNotFoundByCodeException;
import com.dev.dungcony.modules.notifications.exceptions.NotiUnAuthException;
import com.dev.dungcony.modules.notifications.repositories.NotificationRepository;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationUpdateImpl implements NotificationUpdateService {

    private final NotificationRepository notificationRepo;

    @Transactional
    @Override
    public void userReaded(String code, UUID uid) {
        Notification noti = notificationRepo.findByCode(code)
                .orElseThrow(NotiNotFoundByCodeException::new);

        if (!Boolean.FALSE.equals(noti.getForAdmin()) || !uid.equals(noti.getReceiverId()))
            throw new NotiUnAuthException();

        noti.setReaded(true);
        notificationRepo.save(noti);
    }

    @Transactional
    @Override
    public long userReadAll(UUID uid) {
        return notificationRepo.updateReadAllUser(uid);
    }

    @Transactional
    @Override
    public void adminReaded(String code) {
        Notification noti = notificationRepo.findByCode(code)
                .orElseThrow(NotiNotFoundByCodeException::new);

        if (!Boolean.TRUE.equals(noti.getForAdmin())) {
            throw new NotiUnAuthException();
        }

        noti.setReaded(true);
        notificationRepo.save(noti);
    }

    @Transactional
    @Override
    public long adminReadAll() {
        return notificationRepo.updateReadAllForAdmin();
    }
}
