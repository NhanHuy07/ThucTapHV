package com.dev.dungcony.modules.order.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.order.dtos.req.UpdateOrderStatusReq;
import com.dev.dungcony.modules.order.services.interfaces.OrderUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/order/update")
@Tag(name = "Orders (Admin)")
public class AdminUpdateOrderController {

    private final OrderUpdateService orderUpdateService;

    @Operation(summary = "Cập nhật trạng thái đơn hàng")
    @PutMapping("/status")
    public ResponseEntity<ApiRes<Void>> updateStatus(
            @Valid @RequestBody UpdateOrderStatusReq req) {
        orderUpdateService.adminUpdateOrderStatus(req.orderCode(), req.status());
        return ResponseEntity.ok(ApiRes.success("Order status updated to " + req.status()));
    }

    @Operation(summary = "confirm đơn hàng")
    @PutMapping("/confirm/{code}")
    public ResponseEntity<ApiRes<Void>> confirmOrder(
            @PathVariable String code) {
        orderUpdateService.adminConfirmOrder(code);
        return ResponseEntity.ok(ApiRes.success("Order confirm success"));
    }

    @Operation(summary = "shipping đơn hàng")
    @PutMapping("/shipping/{code}")
    public ResponseEntity<ApiRes<Void>> shippingOrder(
            @PathVariable String code) {
        orderUpdateService.adminShippingOrder(code);
        return ResponseEntity.ok(ApiRes.success("Order shipping success"));
    }

    @Operation(summary = "confirm đơn hàng")
    @PutMapping("/delivered/{code}")
    public ResponseEntity<ApiRes<Void>> deliveryOrder(
            @PathVariable String code) {
        orderUpdateService.adminDeliveredOrder(code);
        return ResponseEntity.ok(ApiRes.success("Order delivered success"));
    }

}
