package com.dev.dungcony.modules.notifications.services.impl;

import com.dev.dungcony.modules.notifications.dtos.req.AdminCreateNotificationReq;
import com.dev.dungcony.modules.notifications.dtos.req.UserNotificationCreateReq;
import com.dev.dungcony.modules.notifications.entities.Notification;
import com.dev.dungcony.modules.notifications.enums.NotificationType;
import com.dev.dungcony.modules.notifications.exceptions.NotiUnAuthException;
import com.dev.dungcony.modules.notifications.mappers.NotiMapper;
import com.dev.dungcony.modules.notifications.repositories.NotificationRepository;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationCreateImpl implements NotificationCreateService {

    private final NotificationRepository notificationRepository;

    @Override
    public String userCreate(UUID senderId, UserNotificationCreateReq req) {
        if (!senderId.equals(req.senderId()))
            throw new NotiUnAuthException();

        Notification noti = NotiMapper.toEntity(req);

        notificationRepository.save(noti);

        return noti.getCode();
    }

    @Override
    public void userCreateOrder(UUID uid) {
        UserNotificationCreateReq req = new UserNotificationCreateReq(
                uid,
                NotificationType.ORDER_CREATED,
                "create_order",
                "tạo đơn hàng"
        );
        Notification noti = NotiMapper.toEntity(req);

        notificationRepository.save(noti);
    }


    @Override
    public void userPailOrder(UUID uid) {
        UserNotificationCreateReq req = new UserNotificationCreateReq(
                uid,
                NotificationType.ORDER_PAID,
                "paid_order",
                "thanh toán đơn hàng"
        );
        Notification noti = NotiMapper.toEntity(req);

        notificationRepository.save(noti);
    }

    @Override
    public void userCancelOrder(UUID uid) {
        UserNotificationCreateReq req = new UserNotificationCreateReq(
                uid,
                NotificationType.ORDER_CANCELLED,
                "cancel_order",
                "hủy đơn hàng"
        );

        Notification noti = NotiMapper.toEntity(req);

        notificationRepository.save(noti);
    }

    @Transactional
    @Override
    public List<String> adminCreate(AdminCreateNotificationReq req) {

        List<Notification> notis = NotiMapper.toEntity(req);

        if (notis.isEmpty()) {
            return Collections.emptyList();
        }

        List<Notification> savedNotis = notificationRepository.saveAll(notis);

        return savedNotis.stream()
                .map(Notification::getCode)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<String> adminComfirmOrders(List<UUID> uids) {
        AdminCreateNotificationReq req = new AdminCreateNotificationReq(
                uids,
                "CONFIRM_ORDER",
                "đơn hàng của bạn đã được xác nhận"
        );

        List<Notification> notis = NotiMapper.toEntity(req);

        if (notis.isEmpty()) {
            return Collections.emptyList();
        }

        List<Notification> savedNotis = notificationRepository.saveAll(notis);

        return savedNotis.stream()
                .map(Notification::getCode)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<String> adminDeliveredOrders(List<UUID> uids) {

        AdminCreateNotificationReq req = new AdminCreateNotificationReq(
                uids,
                "DELIVERED_ORDER",
                "đơn hàng của bạn đã được giao vui lòng xác nhận"
        );

        List<Notification> notis = NotiMapper.toEntity(req);

        if (notis.isEmpty()) {
            return Collections.emptyList();
        }

        List<Notification> savedNotis = notificationRepository.saveAll(notis);

        return savedNotis.stream()
                .map(Notification::getCode)
                .filter(Objects::nonNull)
                .toList();
    }
}
