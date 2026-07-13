package com.dev.dungcony.modules.notifications.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/notification/update")
@Tag(name = "Notifications(Admin)")
public class AdminNotiUpdateController {

    private final NotificationUpdateService notificationUpdateService;

    @Operation(summary = "Đánh dấu 1 thông báo đã đọc")
    @PutMapping("/read/{code}")
    public ResponseEntity<ApiRes<Void>> markRead(@PathVariable String code) {
        notificationUpdateService.adminReaded(code);
        return ResponseEntity.ok(ApiRes.success("Đã đọc"));
    }

    @Operation(summary = "Đánh dấu tất cả đã đọc (admin)")
    @PutMapping("/read-all")
    public ResponseEntity<ApiRes<Long>> markAllRead() {
        long readCount = notificationUpdateService.adminReadAll();
        return ResponseEntity.ok()
                .body(ApiRes.success(
                        "Đã đọc " + readCount + " thông báo",
                        readCount
                ));
    }
}
