package com.dev.dungcony.modules.notifications.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.notifications.dtos.req.DeleteListNotiReq;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationDeleteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/notification")
@Tag(name = "Notifications(Admin)")
public class AdminNotiDeleteController {

    private final NotificationDeleteService notificationDeleteService;

    @Operation(summary = "xoas thông báo admin", description = "Đơn hàng mới, đơn hủy, v.v.")
    @PutMapping("/delete/{code}")
    public ResponseEntity<ApiRes<Void>> deleteNotification(@PathVariable String code) {

        notificationDeleteService.adminDeleteByCode(code);

        return ResponseEntity.ok()
                .body(ApiRes.success("Delete notification"));
    }

    @Operation(summary = "xóa nhiều thông báo admin", description = "Đơn hàng mới, đơn hủy, v.v.")
    @PutMapping("/delete/list")
    public ResponseEntity<ApiRes<Void>> deleteNotification
            (
                    @Valid @RequestBody DeleteListNotiReq req
            ) {

        int cnt = notificationDeleteService.adminDeleteListByCode(req.notis());

        return ResponseEntity.ok()
                .body(ApiRes.success("Delete " + cnt + " notification"));
    }

    @Operation(summary = "dọn dẹp bảng thông báo admin", description = "Đơn hàng mới, đơn hủy, v.v.")
    @PutMapping("/clear")
    public ResponseEntity<ApiRes<Void>> clear() {
        int cnt = notificationDeleteService.adminClear();

        return ResponseEntity.ok()
                .body(ApiRes.success("clear thành công đã xóa " + cnt + " thông báo"));
    }
}
