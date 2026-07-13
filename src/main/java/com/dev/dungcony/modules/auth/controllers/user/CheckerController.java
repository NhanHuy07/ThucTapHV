package com.dev.dungcony.modules.auth.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.services.interfaces.AccountCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/account/check")
@Tag(name = "Auth")
public class CheckerController {

    private final AccountCheckService accountCheckService;

    @Operation(
            summary = "Kiểm tra email đã tồn tại chưa",
            description = """
                    Dùng để kiểm tra realtime khi user điền form đăng ký.
                    
                    **Trả về:**
                    - `200`: email **chưa tồn tại** (có thể dùng để đăng ký)
                    - `200 + message khác` hoặc logic FE tự xử lý: email **đã tồn tại**
                    
                    **Lưu ý:** API này không yêu cầu đăng nhập (public).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kiểm tra thành công",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": true, "message": "check email", "data": null }
                            """))
            )
    })
    @GetMapping("/exists-email")
    public ResponseEntity<ApiRes<Void>> checkEmail(
            @Parameter(description = "Email cần kiểm tra", required = true, example = "user@example.com")
            @RequestParam String email) {
        accountCheckService.existsByEmail(email);
        return ResponseEntity.ok().body(ApiRes.success("check email"));
    }

    @Operation(
            summary = "Kiểm tra username đã tồn tại chưa",
            description = """
                    Dùng để kiểm tra realtime khi user điền form đăng ký.
                    
                    **Lưu ý:** API này không yêu cầu đăng nhập (public).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kiểm tra thành công",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": true, "message": "check username", "data": null }
                            """))
            )
    })
    @GetMapping("/exists-username")
    public ResponseEntity<ApiRes<Void>> checkUsername(
            @Parameter(description = "Username cần kiểm tra", required = true, example = "dungcony")
            @RequestParam String username) {
        accountCheckService.existsByUsername(username);
        return ResponseEntity.ok().body(ApiRes.success("check username"));
    }

    @Operation(
            summary = "Kiểm tra mật khẩu hiện tại có đúng không",
            description = """
                    Dùng để xác nhận danh tính trước khi thực hiện các thao tác nhạy cảm (ví dụ: trước khi hiển thị form đổi mật khẩu).
                    
                    **Yêu cầu:** Bearer token trong header `Authorization`.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mật khẩu đúng",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": true, "message": "password correct", "data": null }
                            """))
            ),
            @ApiResponse(responseCode = "401", description = "Mật khẩu sai",
                    content = @Content(examples = @ExampleObject(value = """
                            { "success": false, "message": "Invalid username or password", "data": null }
                            """))
            ),
            @ApiResponse(responseCode = "403", description = "Chưa đăng nhập hoặc token hết hạn")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/password")
    public ResponseEntity<ApiRes<Void>> checkPass(
            @Parameter(hidden = true) @AuthenticationPrincipal AccountDetails detail,
            @Parameter(description = "Mật khẩu cần kiểm tra", required = true, example = "mypassword123")
            @RequestBody String password) {
        accountCheckService.checkPassword(detail.getId(), password);
        return ResponseEntity.ok().body(ApiRes.success("password correct"));
    }
}
