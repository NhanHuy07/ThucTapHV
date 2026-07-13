package com.dev.dungcony.modules.notifications.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.dtos.req.DeleteListNotiReq;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationDeleteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/notifications")
@Tag(name = "Notifications")
public class NotiUserDeleteController {

        private final NotificationDeleteService notificationDeleteService;

        @Operation(summary = "user xoas thông báo", description = "Đơn hàng mới, đơn hủy, v.v.")
        @PutMapping("/delete/{code}")
        public ResponseEntity<ApiRes<Void>> deleteNotification(
                        @AuthenticationPrincipal AccountDetails user,
                        @PathVariable String code) {

                notificationDeleteService.deleteByCode(user.getUserUuid(), code);

                return ResponseEntity.ok()
                                .body(ApiRes.success("Delete notification"));
        }

        @Operation(summary = "user xóa nhiều thông báo", description = "Đơn hàng mới, đơn hủy, v.v.")
        @PutMapping("/delete/list")
        public ResponseEntity<ApiRes<Void>> deleteNotification(
                        @AuthenticationPrincipal AccountDetails user,
                        @Valid @RequestBody DeleteListNotiReq req) {

                int cnt = notificationDeleteService.deleteListByCode(user.getUserUuid(), req.notis());

                return ResponseEntity.ok()
                                .body(ApiRes.success("Delete " + cnt + " notification"));
        }

        @Operation(summary = "use dọn dẹp bảng thông báo", description = "Đơn hàng mới, đơn hủy, v.v.")
        @PutMapping("/clear")
        public ResponseEntity<ApiRes<Void>> clear(
                        @AuthenticationPrincipal AccountDetails user) {
                int cnt = notificationDeleteService.clear(user.getUserUuid());

                return ResponseEntity.ok()
                                .body(ApiRes.success("clear thành công đã xóa " + cnt + " thông báo"));
        }
}
