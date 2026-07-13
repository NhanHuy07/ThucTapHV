package com.dev.dungcony.modules.auth.controllers.user;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.dtos.req.RegisReq;
import com.dev.dungcony.modules.auth.dtos.req.VerifyOtpReq;
import com.dev.dungcony.modules.auth.services.interfaces.AuthService;
import com.dev.dungcony.modules.auth.services.interfaces.VerifyOtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/public/auth/regis")
@Tag(name = "Auth")
public class RegisController {

    private final AuthService authService;
    private final VerifyOtpService verifyOtpService;

    @Operation(summary = "Bước 1 — Tạo tài khoản", description = """
            Tạo tài khoản mới với email, username và password.
            
            **Flow đăng ký:**
            1. Gọi API này → hệ thống tạo tài khoản (chưa kích hoạt) và gửi mã OTP 6 số về email
            2. Gọi `POST /regis/verify` với mã OTP vừa nhận để kích hoạt tài khoản
            
            **Lưu ý:** Tài khoản chưa verify OTP sẽ không thể đăng nhập.
            
            **Ràng buộc:**
            - `email`: đúng định dạng email, chưa tồn tại trong hệ thống
            - `username`: 3–50 ký tự, chưa tồn tại trong hệ thống
            - `password`: 8–50 ký tự
            """)
    @PostMapping("")
    public ResponseEntity<ApiRes<Void>> register(@Valid @RequestBody RegisReq req) {
        log.info("Register req: {}", req);
        authService.register(req);
        return ResponseEntity.ok().body(ApiRes.success("register success"));
    }

    @Operation(summary = "Bước 2 — Xác thực OTP kích hoạt tài khoản", description = """
            Nhập mã OTP 6 số đã được gửi về email để kích hoạt tài khoản.
            
            **Lưu ý:**
            - OTP có hiệu lực trong **5 phút**
            - Sau khi verify thành công, tài khoản được kích hoạt và có thể đăng nhập
            - Nếu OTP hết hạn, cần đăng ký lại từ bước 1
            """)

    @PostMapping("/verify")
    public ResponseEntity<ApiRes<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpReq req) {
        verifyOtpService.verifyOtpRegister(req);
        return ResponseEntity.ok().body(ApiRes.success("register success"));
    }
}
