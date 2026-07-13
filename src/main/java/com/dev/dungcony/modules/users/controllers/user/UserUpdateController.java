package com.dev.dungcony.modules.users.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.dto.req.UserUpdateReq;
import com.dev.dungcony.modules.users.services.interfaces.users.UserUpdateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/user/update")
public class UserUpdateController {

    private final UserUpdateService userUpdate;

    @Operation(summary = "Update my profile")
    @PutMapping("/profile")
    public ResponseEntity<ApiRes<UserRes>> updateProfile(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody UserUpdateReq req) {
        return ResponseEntity.ok()
                .body(ApiRes.success("updated", userUpdate.updateUser(details.getId(), req)));
    }
}
