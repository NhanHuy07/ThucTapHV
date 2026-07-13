package com.dev.dungcony.modules.order.controllers.user;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dungcony.commons.dtos.AccountDetails;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/order")
@Tag(name = "Orders")
public class OrderUserGetController {

    private final OrderGetService orderGetService;


    @Operation(summary = "Lấy danh sách đơn hàng")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiRes<PageRes<OrderSummaryRes>>> getMyOrders(
            @AuthenticationPrincipal AccountDetails account,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("Orders retrieved",
                        PageRes.from(orderGetService.userGetOrders(account.requireUserUuid(), pageable))));
    }

    @Operation(summary = "Lấy danh sách đơn hàng theo trạng thái")
    @GetMapping("/my-orders/status")
    public ResponseEntity<ApiRes<PageRes<OrderSummaryRes>>> getMyOrdersByStatus(
            @AuthenticationPrincipal AccountDetails account,
            @Parameter(description = "Trạng thái đơn hàng") @RequestParam("status") OrderStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success("Orders retrieved",
                        PageRes.from(orderGetService.userGetOrdersByStatus(account.requireUserUuid(), status, pageable))));
    }

    @Operation(summary = "Xem chi tiết đơn hàng")
    @GetMapping("/{orderCode}")
    public ResponseEntity<ApiRes<OrderRes>> getOrderDetail(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable String orderCode) {
        return ResponseEntity.ok(
                ApiRes.success("Order detail",
                        orderGetService.userGetOrderByCode(account.requireUserUuid(), orderCode)));
    }

}
