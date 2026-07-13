package com.dev.dungcony.modules.order.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.order.dtos.req.CreateOrderReq;
import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.services.interfaces.OrderCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/order")
@Tag(name = "Orders")
public class OrderUserCreateController {

    private final OrderCreateService orderCreateService;

    @Operation(summary = "Tạo đơn hàng từ giỏ hàng",
            description = "Tạo đơn hàng từ các sản phẩm đã chọn trong giỏ. Nếu paymentType=ONLINE sẽ trả kèm paymentUrl")
    @PostMapping("/create")
    public ResponseEntity<ApiRes<OrderRes>> createOrder(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody CreateOrderReq req,
            HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        OrderRes order = orderCreateService.userCreateOrder(account.requireUserUuid(), req, ipAddress);
        return ResponseEntity.ok(ApiRes.success("Order created successfully", order));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String ip = request.getRemoteAddr();
        return ip != null ? ip : "127.0.0.1";
    }
}
