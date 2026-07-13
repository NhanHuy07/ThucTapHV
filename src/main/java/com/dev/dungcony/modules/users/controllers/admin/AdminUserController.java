package com.dev.dungcony.modules.users.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.users.dto.req.UserUpdateReq;
import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.services.interfaces.users.UserGetService;
import com.dev.dungcony.modules.users.services.interfaces.users.UserUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/user")
@Tag(name = "Users (Admin)")
public class AdminUserController {

    private final UserGetService userGetService;
    private final UserUpdateService userUpdateService;

    @Operation(summary = "Lấy tất cả user", description = "Phân trang, hỗ trợ sort")
    @GetMapping("/get-all")
    public ResponseEntity<ApiRes<PageRes<UserRes>>> getAll(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("All users",
                        PageRes.from(userGetService.getAll(pageable))));
    }

    @Operation(summary = "Lấy user theo id")
    @GetMapping("/get-by-id")
    public ResponseEntity<ApiRes<UserRes>> getById(
            @RequestParam UUID userId) {
        return ResponseEntity.ok(
                ApiRes.success("user",
                        userGetService.getUserById(userId)));
    }

    @Operation(summary = "Lấy user theo tên")
    @GetMapping("/get-by-name")
    public ResponseEntity<ApiRes<?>> getByName(
            @RequestParam String name) {
        return ResponseEntity.ok(
                ApiRes.success("user",
                        userGetService.getByName(name)));
    }

    @Operation(summary = "Cập nhật thông tin user")
    @PutMapping("/update")
    public ResponseEntity<ApiRes<UserRes>> update(
            @Valid @RequestBody UserUpdateReq req) {
        return ResponseEntity.ok(
                ApiRes.success("updated",
                        userUpdateService.adminUpdateUser(req)));
    }
}
