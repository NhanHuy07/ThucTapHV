package com.dev.dungcony.modules.notifications.dtos.req;

import com.dev.dungcony.modules.notifications.enums.NotificationType;

import java.util.UUID;

public record UserNotificationCreateReq
        (
                UUID senderId,
                NotificationType type,
                String title,
                String message
        ) {
}
