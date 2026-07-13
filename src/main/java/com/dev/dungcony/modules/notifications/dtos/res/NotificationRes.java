package com.dev.dungcony.modules.notifications.dtos.res;

public record NotificationRes(
        String code,
        String senderName,
        String receiverName,
        String title,
        String message
) {
}
