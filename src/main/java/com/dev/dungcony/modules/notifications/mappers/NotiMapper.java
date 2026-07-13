package com.dev.dungcony.modules.notifications.mappers;

import com.dev.dungcony.modules.notifications.dtos.req.AdminCreateNotificationReq;
import com.dev.dungcony.modules.notifications.dtos.req.UserNotificationCreateReq;
import com.dev.dungcony.modules.notifications.entities.Notification;
import com.dev.dungcony.modules.notifications.enums.NotificationType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class NotiMapper {
    public static Notification toEntity(UserNotificationCreateReq req) {
        Notification noti = new Notification();
        noti.setMessage(req.message());
        noti.setTitle(req.title());
        noti.setSenderId(req.senderId());
        noti.setReceiverId(null);
        noti.setForAdmin(true);
        noti.setType(req.type());

        noti.setCode(generateUniqueCode(noti.getSenderId(), noti.getReceiverId()));

        return noti;
    }

    public static List<Notification> toEntity(AdminCreateNotificationReq req) {
        List<Notification> notis = new ArrayList<Notification>();

        for (UUID uid : req.receivers()) {
            Notification noti = new Notification();
            noti.setSenderId(null);
            noti.setReceiverId(uid);

            noti.setMessage(req.message());
            noti.setTitle(req.title());
            noti.setForAdmin(false);
            noti.setType(NotificationType.SYSTEM);

            noti.setCode(generateUniqueCode(noti.getSenderId(), noti.getReceiverId()));

            notis.add(noti);
        }
        return notis;
    }


    //---------------------------- generate code ---------------------------//
    private static String generateUniqueCode(UUID senderId, UUID receiverId) {
        long timestamp = System.currentTimeMillis();
        String combined = String.valueOf(senderId) + String.valueOf(receiverId) + timestamp;

        // Tạo hash 16 ký tự để giảm khả năng trùng
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
    }
}
