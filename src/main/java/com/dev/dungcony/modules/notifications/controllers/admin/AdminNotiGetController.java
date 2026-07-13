package com.dev.dungcony.modules.notifications.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/notification/get")
@Tag(name = "Notifications(Admin)")
public class AdminNotiGetController {

    private final NotificationGetService notificationGetService;

    @Operation(summary = "Lấy thông báo admin", description = "Đơn hàng mới, đơn hủy, v.v.")
    @GetMapping("")
    public ResponseEntity<ApiRes<?>> getNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllForAdmin(pageable))));
    }

    @Operation(summary = "Đếm thông báo chưa đọc (admin)")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiRes<Map<String, Long>>> unreadCount() {
        long count = notificationGetService.countUnRead();
        return ResponseEntity.ok(ApiRes.success("OK", Map.of("unread", count)));
    }

    @Operation(summary = "Lấy thông báo chưa đọc (admin)")
    @GetMapping("/unread")
    public ResponseEntity<ApiRes<?>> unread(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllUnreadByForAdmin(pageable))));
    }

    @Operation(summary = "Lấy thông báo đã đọc (admin)")
    @GetMapping("/read")
    public ResponseEntity<ApiRes<?>> read(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok().body(ApiRes.success(
                "OK",
                PageRes.from(notificationGetService.getAllUnreadByForAdmin(pageable))));
    }
}
