package com.dev.dungcony.modules.order.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.services.interfaces.OrderGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/order/get")
@Tag(name = "Orders (Admin)")
public class AdminGetOrderController {

    private final OrderGetService orderGetService;

    @Operation(summary = "Lấy tất cả đơn hàng", description = "Phân trang, hỗ trợ sort")
    @GetMapping("/all")
    public ResponseEntity<ApiRes<PageRes<OrderSummaryRes>>> getAllOrders(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("All orders",
                        PageRes.from(orderGetService.adminGetAllOrders(pageable))));
    }

    @Operation(summary = "Lấy đơn hàng theo trạng thái")
    @GetMapping("/by-status")
    public ResponseEntity<ApiRes<PageRes<OrderSummaryRes>>> getOrdersByStatus(
            @Parameter(description = "Trạng thái đơn hàng") @RequestParam("status") OrderStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("Orders by status",
                        PageRes.from(orderGetService.adminGetAllOrdersByStatus(status, pageable))));
    }

    @Operation(summary = "Xem chi tiết đơn hàng")
    @GetMapping("/{orderCode}")
    public ResponseEntity<ApiRes<OrderRes>> getOrderDetail(
            @PathVariable String orderCode) {
        return ResponseEntity.ok(
                ApiRes.success("Order detail",
                        orderGetService.adminGetOrderByCode(orderCode)));
    }
}
