package com.dev.dungcony.modules.voucher.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/user-vouchers")
@Tag(name = "Voucher (Admin)")
public class AdminUserVoucherController {

    private final UserVoucherGetService userVoucherGetService;

    @Operation(summary = "Lấy voucher của user theo id", description = "Lấy voucher của user theo id")
    @GetMapping("/get-by-id")
    public ResponseEntity<ApiRes<?>> getUserVouchersById(
            @RequestParam(required = false) UUID uid) {
        return ResponseEntity.ok(ApiRes.success(
                "User vouchers",
                userVoucherGetService.getUserVouchers(uid)));
    }

    @Operation(summary = "Lấy voucher của user theo name", description = "Lấy voucher của user theo name")
    @GetMapping("/get-by-name")
    public ResponseEntity<ApiRes<?>> getUserVouchersByName(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(ApiRes.success(
                "User vouchers",
                userVoucherGetService.getUserVouchersByName(name)));
    }
}
