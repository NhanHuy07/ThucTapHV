package com.dev.dungcony.modules.notifications.services.impl;

import com.dev.dungcony.modules.notifications.dtos.res.NotificationRes;
import com.dev.dungcony.modules.notifications.repositories.NotificationRepository;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationGetImpl implements NotificationGetService {

    private final NotificationRepository notificationRepo;

    @Override
    public Page<NotificationRes> getAllByReceiver(Pageable pageable, UUID receiverId) {
        return notificationRepo.findAllByReceiver(receiverId, pageable);
    }

    @Override
    public Page<NotificationRes> getAllUnreadByForReceiver(Pageable pageable, UUID receiverId) {
        return notificationRepo.findAllForReciverUnread(receiverId, pageable);
    }

    @Override
    public Page<NotificationRes> getAllReadByForReceiver(Pageable pageable, UUID receiverId) {
        return notificationRepo.findAllForReceiverReaded(receiverId, pageable);
    }

    @Override
    public Page<NotificationRes> getAllForAdmin(Pageable pageable) {
        return notificationRepo.findAllForAdmin(pageable);
    }

    @Override
    public Page<NotificationRes> getAllUnreadByForAdmin(Pageable pageable) {
        return notificationRepo.findAllForAdminUnread(pageable);
    }

    @Override
    public Page<NotificationRes> getAllReadByForAdmin(Pageable pageable) {
        return notificationRepo.findAllForAdminReaded(pageable);
    }

    @Override
    public long countUnRead(UUID receiverId) {
        return notificationRepo.countByReceiverIdAndForAdminFalseAndReadedFalseAndIsDeleteFalse(receiverId);
    }


    @Override
    public long countUnRead() {
        return notificationRepo.countByForAdminTrueAndReadedFalseAndIsDeleteFalse();
    }
}
