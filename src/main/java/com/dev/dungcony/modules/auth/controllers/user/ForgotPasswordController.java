package com.dev.dungcony.modules.auth.controllers.user;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.services.interfaces.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/public/auth/forgot-password")
@Tag(name = "Auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @Operation(
            summary = "Quên mật khẩu",
            description = """
                    Hệ thống tạo mật khẩu mới ngẫu nhiên và gửi về email đã đăng ký.
                    
                    **Lưu ý:**
                    - Email phải đã tồn tại trong hệ thống
                    - Sau khi nhận mật khẩu mới, nên đổi lại mật khẩu ngay tại `POST /v1/api/account/update/password`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mật khẩu mới đã được gửi về email",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": true, "message": "New password sent to your email", "data": null }
                            """))
            ),
            @ApiResponse(responseCode = "404", description = "Email không tồn tại trong hệ thống",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": false, "message": "not found account", "data": null }
                            """))
            )
    })
    @PostMapping
    public ResponseEntity<ApiRes<Void>> forgotPassword(
            @Parameter(description = "Email đã đăng ký tài khoản", required = true, example = "user@example.com")
            @RequestParam @NotBlank @Email String email) {
        log.info("Forgot password request for email: {}", email);
        forgotPasswordService.forgotPassword(email);
        return ResponseEntity.ok().body(ApiRes.success("New password sent to your email"));
    }
}
