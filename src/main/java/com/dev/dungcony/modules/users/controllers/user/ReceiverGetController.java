package com.dev.dungcony.modules.users.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.services.interfaces.receivers.RecieverGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Receiver")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/user/receiver/get")
public class ReceiverGetController {

    private final RecieverGetService recieverGetService;

    @Operation(summary = "Get my receivers")
    @GetMapping("/all")
    public ResponseEntity<ApiRes<List<ReceiverRes>>> getAll(
            @AuthenticationPrincipal AccountDetails details) {
        return ResponseEntity.ok(
                ApiRes.success("receivers", recieverGetService.getAllByUser(details.getUserUuid())));
    }

    @Operation(summary = "Get receiver by id")
    @GetMapping
    public ResponseEntity<ApiRes<ReceiverRes>> getById(
            @AuthenticationPrincipal AccountDetails details,
            @RequestParam Integer receiverId) {
        return ResponseEntity.ok(
                ApiRes.success("receiver", recieverGetService.getReceiverById(details.getUserUuid(), receiverId)));
    }
}
