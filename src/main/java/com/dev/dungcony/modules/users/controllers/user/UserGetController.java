package com.dev.dungcony.modules.users.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.services.interfaces.users.UserGetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/user/get")
public class UserGetController {
    private final UserGetService userGetService;

    @Operation(summary = "Get my profile")
    @GetMapping("/me")
    public ResponseEntity<ApiRes<UserRes>> getMe(
            @AuthenticationPrincipal AccountDetails details) {
        return ResponseEntity.ok()
                .body(ApiRes.success("profile", userGetService.getUserByAccId(details.getId())));
    }

}
