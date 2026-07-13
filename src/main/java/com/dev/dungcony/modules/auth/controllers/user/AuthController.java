package com.dev.dungcony.modules.auth.controllers.user;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.dtos.req.LoginReq;
import com.dev.dungcony.modules.auth.dtos.res.AcessTokenRes;
import com.dev.dungcony.modules.auth.dtos.res.LoginResult;
import com.dev.dungcony.modules.auth.services.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/public/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Đăng nhập", description = "Đăng nhập bằng username và password")
    @PostMapping("/login")
    public ResponseEntity<ApiRes<AcessTokenRes>> login(
            @Valid @RequestBody LoginReq loginReq,
            @Parameter(description = "ID định danh thiết bị, FE tự sinh UUID và lưu lại", required = true, example = "device-uuid-abc123") @RequestHeader("X-Device-Id") String deviceId) {
        log.info("Login req: {}", loginReq);

        LoginResult res = authService.login(loginReq.username(), loginReq.password(), deviceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, res.refreshToken())
                .body(ApiRes.success("login success", new AcessTokenRes(res.token(), res.expired())));
    }

    @Operation(summary = "Refresh token", description = "Lấy access token mới bằng refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiRes<AcessTokenRes>> refresh(
            @CookieValue(value = "refresh_token", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiRes.error("Missing refresh_token cookie"));
        }

        return ResponseEntity.ok()
                .body(ApiRes.success("refresh_success", authService.refreshToken(token)));
    }

    @Operation(summary = "Đăng xuất", description = "Xóa refresh token của thiết bị hiện tại khỏi Redis")
    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Void>> logout(
            @Parameter(description = "Cookie refresh_token", required = true, example = "<refresh_token_cookie_value>") @CookieValue(value = "refresh_token", required = false) String token,
            @Parameter(description = "ID thiết bị, phải khớp với lúc đăng nhập", required = true, example = "device-uuid-abc123") @RequestHeader("X-Device-Id") String deviceId) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiRes.error("Missing refresh_token cookie"));
        }

        log.info("Logout token: {}", token);
        authService.logout(token, deviceId);
        return ResponseEntity.ok().body(ApiRes.success("logout success"));
    }
}
