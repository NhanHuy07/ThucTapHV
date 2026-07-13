package com.dev.dungcony.modules.notifications.services.interfaces;

import com.dev.dungcony.modules.notifications.dtos.res.NotificationRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationGetService {
    Page<NotificationRes> getAllByReceiver(Pageable pageable, UUID receiverId);

    Page<NotificationRes> getAllUnreadByForReceiver(Pageable pageable, UUID receiverId);

    Page<NotificationRes> getAllReadByForReceiver(Pageable pageable, UUID receiverId);

    Page<NotificationRes> getAllForAdmin(Pageable pageable);

    Page<NotificationRes> getAllUnreadByForAdmin(Pageable pageable);

    Page<NotificationRes> getAllReadByForAdmin(Pageable pageable);
    
    long countUnRead(UUID receiverId);

    long countUnRead();
}
