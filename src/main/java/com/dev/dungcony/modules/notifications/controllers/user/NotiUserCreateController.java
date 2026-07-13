package com.dev.dungcony.modules.notifications.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.dtos.req.UserNotificationCreateReq;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/notifications/create")
@Tag(name = "Notifications")
public class NotiUserCreateController {
    private final NotificationCreateService notificationCreateService;

    @Operation(summary = "user tạo thông báo", description = "Đơn hàng mới, đơn hủy, v.v.")
    @PostMapping("")
    public ResponseEntity<ApiRes<String>> userCreate(
            @AuthenticationPrincipal AccountDetails accId,
            @RequestBody UserNotificationCreateReq req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiRes.success("",
                        notificationCreateService.userCreate(accId.getUserUuid(), req)
                ));
    }
}
