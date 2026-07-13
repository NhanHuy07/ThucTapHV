package com.dev.dungcony.modules.users.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.services.interfaces.receivers.ReceiverRemoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Receiver")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/user/receiver/remove")
public class ReceiverRemoveController {

    private final ReceiverRemoveService receiverRemoveService;

    @Operation(summary = "Remove receiver by id")
    @DeleteMapping
    public ResponseEntity<ApiRes<Void>> removeById(
            @AuthenticationPrincipal AccountDetails details,
            @RequestParam Integer receiverId) {
        receiverRemoveService.removeReceiverUserById(details.getUserUuid(), receiverId);
        return ResponseEntity.ok(ApiRes.success("deleted"));
    }

    @Operation(summary = "Remove all my receivers")
    @DeleteMapping("/all")
    public ResponseEntity<ApiRes<Void>> removeAllByUser(
            @AuthenticationPrincipal AccountDetails details) {
        receiverRemoveService.removeAllByUser(details.getUserUuid());
        return ResponseEntity.ok(ApiRes.success("deleted"));
    }
}
