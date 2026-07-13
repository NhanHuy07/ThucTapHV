package com.dev.dungcony.modules.users.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.dto.req.ReceiverCreateReq;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.services.interfaces.receivers.ReceiverCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Receiver")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/user/receiver/create")
public class ReceiverCreateController {

    private final ReceiverCreateService receiverCreateService;

    @Operation(summary = "Create new receiver")
    @PostMapping
    public ResponseEntity<ApiRes<ReceiverRes>> create(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody ReceiverCreateReq req) {
        return ResponseEntity.ok(
                ApiRes.success("created", receiverCreateService.create(details.getUserUuid(), req)));
    }
}
