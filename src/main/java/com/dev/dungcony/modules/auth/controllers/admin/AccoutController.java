package com.dev.dungcony.modules.auth.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.auth.dtos.req.LoginReq;
import com.dev.dungcony.modules.auth.services.interfaces.AccountCreateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/public/auth")
@Tag(name = "Auth")
public class AccoutController {
    private final AccountCreateService accountCreateService;

    @PostMapping("create-admin")
    public ResponseEntity<ApiRes<?>> cAdmin(LoginReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiRes.success(accountCreateService.createAdminAccount(req.username(), req.password())));
    }
}
