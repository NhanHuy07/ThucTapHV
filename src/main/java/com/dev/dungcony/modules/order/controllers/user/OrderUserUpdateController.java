package com.dev.dungcony.modules.order.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.order.services.interfaces.OrderUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/order")
@Tag(name = "Orders")
public class OrderUserUpdateController {

    private final OrderUpdateService orderUpdateService;

    @Operation(summary = "Hủy đơn hàng", description = "Chỉ hủy được đơn hàng ở trạng thái PENDING")
    @PutMapping("/cancel/{order-code}")
    public ResponseEntity<ApiRes<Void>> cancelOrder(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable("order-code") String orderCode) {
        orderUpdateService.userCancelOrder(account.requireUserUuid(), orderCode);
        return ResponseEntity.ok(ApiRes.success("Order cancelled"));
    }

    @Operation(summary = "xác nhận đã nhận được đơn hàng", description = "")
    @PutMapping("/complete/{order-code}")
    public ResponseEntity<ApiRes<Void>> completeOrder(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable("order-code") String orderCode) {
        orderUpdateService.userCompleteOrder(account.requireUserUuid(), orderCode);
        return ResponseEntity.ok(ApiRes.success("Order completed"));
    }

}
