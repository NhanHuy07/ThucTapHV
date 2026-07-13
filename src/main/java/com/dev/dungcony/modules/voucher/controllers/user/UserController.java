package com.dev.dungcony.modules.voucher.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.voucher.dto.res.UserVoucherRes;
import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherGetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/vouchers")
@Tag(name = "Vouchers")
public class UserController {

    private final UserVoucherGetService userVoucherGetService;

    @GetMapping
    public ResponseEntity<ApiRes<List<UserVoucherRes>>> getMyVouchers(
            @AuthenticationPrincipal AccountDetails account) {
        return ResponseEntity.ok(ApiRes.success(
                "User vouchers",
                userVoucherGetService.getUserVouchersByStatus(account.requireUserUuid(), UserVoucherStatus.AVAILABLE)));
    }
}
