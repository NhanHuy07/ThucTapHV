package com.dev.dungcony.modules.notifications.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/notifications/update")
@Tag(name = "Notifications")
public class NotiUserUpdateController {
    private final NotificationUpdateService notificationUpdateService;

    @Operation(summary = "Đánh dấu 1 thông báo đã đọc")
    @PutMapping("/read/{code}")
    public ResponseEntity<ApiRes<Void>> markRead(
            @AuthenticationPrincipal AccountDetails accountDetails,
            @PathVariable String code) {
        notificationUpdateService.userReaded(code, accountDetails.getUserUuid());
        return ResponseEntity.ok(ApiRes.success("Đã đọc"));
    }

    @Operation(summary = "Đánh dấu tất cả đã đọc")
    @PutMapping("/read-all")
    public ResponseEntity<ApiRes<Long>> markAllRead(
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        long readCount = notificationUpdateService.userReadAll(accountDetails.getUserUuid());
        return ResponseEntity.ok()
                .body(ApiRes.success(
                        "Đã đọc " + readCount + " thông báo",
                        readCount
                ));
    }
}
