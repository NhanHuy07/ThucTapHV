package com.dev.dungcony.modules.auth.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.dtos.req.UpdatePasswordReq;
import com.dev.dungcony.modules.auth.services.interfaces.AccountUpdateService;
import com.dev.dungcony.modules.auth.services.interfaces.SendOtpService;
import com.dev.dungcony.modules.auth.services.interfaces.VerifyOtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/account/update")
@Tag(name = "Auth")
@SecurityRequirement(name = "bearerAuth")
public class UpdateController {

    private final AccountUpdateService accountUpdateService;
    private final SendOtpService sendOtpService;
    private final VerifyOtpService verifyOtpService;

    @Operation(summary = "Bước 1 — Gửi OTP xác nhận đổi email", description = """
            Gửi mã OTP 6 số tới email hiện tại để xác nhận yêu cầu đổi email.
            
            **Flow đổi email:**
            1. Gọi API này với `oldEmail` (email hiện tại của tài khoản) → OTP được gửi về email cũ
            2. Gọi `POST /email/verify-otp` với OTP vừa nhận và `newEmail` muốn đổi sang
            
            **Lưu ý:** `oldEmail` phải khớp với email hiện tại của tài khoản đang đăng nhập.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP đã được gửi về email cũ", content = @Content(examples = @ExampleObject(value = """
                    { "success": true, "message": "send otp req update email successfully", "data": null }
                    """))),
            @ApiResponse(responseCode = "401", description = "Email không khớp với tài khoản đang đăng nhập", content = @Content(examples = @ExampleObject(value = """
                    { "success": false, "message": "Invalid username or password", "data": null }
                    """))),
            @ApiResponse(responseCode = "403", description = "Chưa đăng nhập hoặc token hết hạn")
    })
    @PostMapping("/email/send-otp")
    public ResponseEntity<ApiRes<Void>> sendOtpChangeEmail(
            @Parameter(hidden = true) @AuthenticationPrincipal AccountDetails detail,
            @Parameter(description = "Email hiện tại của tài khoản", required = true, example = "old@example.com") @RequestParam @NotBlank @Email String oldEmail) {
        sendOtpService.sendOtpChangeEmail(detail.getId(), detail.getUsername(), oldEmail);
        return ResponseEntity.ok().body(ApiRes.success("send otp req update email successfully"));
    }

    @Operation(summary = "Bước 2 — Xác nhận OTP và đổi sang email mới", description = """
            Xác nhận OTP đã nhận và cập nhật email mới cho tài khoản.
            
            **Lưu ý:**
            - OTP có hiệu lực trong **5 phút**
            - `newEmail` phải là email chưa được dùng bởi tài khoản khác
            - Sau khi đổi email thành công, tài khoản cần re-verify email mới (nếu có flow verify)
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đổi email thành công", content = @Content(examples = @ExampleObject(value = """
                    { "success": true, "message": "successfully", "data": null }
                    """))),
            @ApiResponse(responseCode = "401", description = "OTP sai hoặc đã hết hạn", content = @Content(examples = @ExampleObject(value = """
                    { "success": false, "message": "OTP is invalid", "data": null }
                    """))),
            @ApiResponse(responseCode = "403", description = "Chưa đăng nhập hoặc token hết hạn")
    })
    @PostMapping("/email/verify-otp")
    public ResponseEntity<ApiRes<Void>> confirmEmailChange(
            @Parameter(hidden = true) @AuthenticationPrincipal AccountDetails detail,
            @Parameter(description = "Email mới muốn đổi sang", required = true, example = "new@example.com") @RequestParam @NotBlank @Email String newEmail,
            @Parameter(description = "Mã OTP 6 số nhận qua email cũ", required = true, example = "123456") @RequestParam @NotBlank String otp) {
        verifyOtpService.verifyOtpEmailChange(detail.getId(), detail.getUsername(), newEmail, otp);
        return ResponseEntity.ok().body(ApiRes.success("successfully"));
    }

    @Operation(summary = "Đổi mật khẩu", description = """
            Đổi mật khẩu bằng cách cung cấp mật khẩu cũ và mật khẩu mới.
            
            **Lưu ý:**
            - `oldPass` phải khớp với mật khẩu hiện tại
            - `newPass`: 8–50 ký tự
            - Sau khi đổi mật khẩu, các session khác vẫn còn hiệu lực đến khi refresh token hết hạn
            """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePasswordReq.class), examples = @ExampleObject(value = """
            {
              "oldPass": "currentpassword123",
              "newPass": "newpassword456"
            }
            """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công", content = @Content(examples = @ExampleObject(value = """
                    { "success": true, "message": "successfully", "data": null }
                    """))),
            @ApiResponse(responseCode = "401", description = "Mật khẩu cũ không đúng", content = @Content(examples = @ExampleObject(value = """
                    { "success": false, "message": "Invalid username or password", "data": null }
                    """))),
            @ApiResponse(responseCode = "403", description = "Chưa đăng nhập hoặc token hết hạn")
    })
    @PostMapping("/password")
    public ResponseEntity<ApiRes<Void>> updatePass(
            @Parameter(hidden = true) @AuthenticationPrincipal AccountDetails detail,
            @Valid @RequestBody UpdatePasswordReq req) {
        accountUpdateService.updatePassword(detail.getId(), req.oldPass(), req.newPass());
        return ResponseEntity.ok().body(ApiRes.success("successfully"));
    }
}
