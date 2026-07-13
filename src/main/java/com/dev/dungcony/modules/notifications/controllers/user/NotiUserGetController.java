package com.dev.dungcony.modules.notifications.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/notifications/get")
@Tag(name = "Notifications")
public class NotiUserGetController {

    private final NotificationGetService notificationGetService;

    @Operation(summary = "Lấy thông báo", description = "Đơn hàng mới, đơn hủy, v.v.")
    @GetMapping("")
    public ResponseEntity<ApiRes<?>> getNotifications(
            @AuthenticationPrincipal AccountDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllByReceiver(pageable, user.getUserUuid()))));
    }

    @Operation(summary = "Đếm thông báo chưa đọc")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiRes<Long>> unreadCount(
            @AuthenticationPrincipal AccountDetails user) {
        long count = notificationGetService.countUnRead(user.getUserUuid());
        return ResponseEntity.ok(ApiRes.success("unread", count));
    }

    @Operation(summary = "Lấy thông báo chưa đọc ")
    @GetMapping("/urnead")
    public ResponseEntity<ApiRes<?>> getNotificationUnread(
            @AuthenticationPrincipal AccountDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllUnreadByForReceiver(pageable, user.getUserUuid()))));
    }

    @Operation(summary = "Lấy thông báo đã đọc")
    @GetMapping("/read")
    public ResponseEntity<ApiRes<?>> getNotificationRead(
            @AuthenticationPrincipal AccountDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllReadByForReceiver(pageable, user.getUserUuid()))));
    }
}
