package com.dev.dungcony.modules.notifications.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.dtos.req.AdminCreateNotificationReq;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/notification/create")
@Tag(name = "Notifications(Admin)")
public class AdminNotiCreateController {

    private final NotificationCreateService notificationCreateService;

    @Operation(summary = "tạo thông báo admin", description = "Đơn hàng mới, đơn hủy, v.v.")
    @PostMapping("/create")
    public ResponseEntity<ApiRes<List<String>>> createForAdmin(
            @Valid @RequestBody AdminCreateNotificationReq req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiRes.success(notificationCreateService.adminCreate(req)));
    }
}
