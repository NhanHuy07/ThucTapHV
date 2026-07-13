package com.dev.dungcony.modules.users.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.services.interfaces.receivers.RecieverGetService;
import com.dev.dungcony.modules.users.services.interfaces.receivers.ReceiverRemoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/receiver")
@Tag(name = "Receivers (Admin)")
public class AdminReceiverController {

    private final RecieverGetService recieverGetService;
    private final ReceiverRemoveService receiverRemoveService;

    @Operation(summary = "Lấy tất cả receiver", description = "Phân trang, hỗ trợ sort")
    @GetMapping("/get-all")
    public ResponseEntity<ApiRes<PageRes<ReceiverRes>>> getAll(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("All receivers",
                        PageRes.from(recieverGetService.adminGetAll(pageable))));
    }

    @Operation(summary = "Lấy receiver theo id")
    @GetMapping
    public ResponseEntity<ApiRes<ReceiverRes>> getById(
            @RequestParam Integer receiverId) {
        return ResponseEntity.ok(
                ApiRes.success("receiver",
                        recieverGetService.adminGetReceiverById(receiverId)));
    }

    @Operation(summary = "Lấy danh sách receiver của user")
    @GetMapping("/by-user")
    public ResponseEntity<ApiRes<List<ReceiverRes>>> getAllByUser(
            @RequestParam UUID userId) {
        return ResponseEntity.ok(
                ApiRes.success("receivers",
                        recieverGetService.getAllByUser(userId)));
    }

    @Operation(summary = "Xóa receiver theo id")
    @DeleteMapping
    public ResponseEntity<ApiRes<Void>> removeById(
            @RequestParam Integer receiverId) {
        receiverRemoveService.removeById(receiverId);
        return ResponseEntity.ok(ApiRes.success("deleted"));
    }

    @Operation(summary = "Xóa tất cả receiver của một user")
    @DeleteMapping("/by-user")
    public ResponseEntity<ApiRes<Void>> removeAllByUser(
            @RequestParam UUID userId) {
        receiverRemoveService.removeAllByUser(userId);
        return ResponseEntity.ok(ApiRes.success("deleted"));
    }

}
