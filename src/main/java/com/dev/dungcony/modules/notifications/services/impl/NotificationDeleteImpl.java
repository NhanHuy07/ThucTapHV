package com.dev.dungcony.modules.notifications.services.impl;

import com.dev.dungcony.modules.notifications.entities.Notification;
import com.dev.dungcony.modules.notifications.exceptions.NotiNotForAdminException;
import com.dev.dungcony.modules.notifications.exceptions.NotiNotFoundByCodeException;
import com.dev.dungcony.modules.notifications.exceptions.NotiUnAuthException;
import com.dev.dungcony.modules.notifications.repositories.NotificationRepository;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationDeleteImpl implements NotificationDeleteService {

    private final NotificationRepository notificationRepository;

    @Override
    public void deleteByCode(UUID uid, String code) {
        Notification noti = notificationRepository.findByCode(code).orElseThrow(NotiNotFoundByCodeException::new);

        if (!Boolean.FALSE.equals(noti.getForAdmin()) || !uid.equals(noti.getReceiverId()))
            throw new NotiUnAuthException();

        noti.setIsDelete(true);
        notificationRepository.save(noti);
    }

    @Transactional
    @Override
    public int deleteListByCode(UUID uid, List<String> codes) {
        int cnt = notificationRepository.deleteByListCodesAndReceiver(codes, uid);
        if (cnt != codes.size())
            throw new NotiUnAuthException();

        return cnt;
    }

    @Override
    public int clear(UUID uid) {
        return notificationRepository.clearByReceiver(uid);
    }

    @Override
    public void adminDeleteByCode(String code) {

        Notification noti = notificationRepository.findByCode(code)
                .orElseThrow(NotiNotFoundByCodeException::new);

        if (!noti.getForAdmin()) {
            throw new NotiNotForAdminException();
        }

        noti.setIsDelete(true);
        notificationRepository.save(noti);

        log.info("Admin đã xóa noti code = {}", code);
    }

    @Override
    public int adminDeleteListByCode(List<String> codes) {
        return notificationRepository.deleteByListCodesForAdmin(codes);
    }

    @Override
    public int adminClear() {
        return notificationRepository.clearByAdmin();
    }
}
