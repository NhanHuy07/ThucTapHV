package com.dev.dungcony.modules.payment.controllers;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.payment.dtos.res.PaymentRes;
import com.dev.dungcony.modules.payment.services.interfaces.PayOsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.model.webhooks.Webhook;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment")
public class PaymentController {

    private final PayOsService payOsService;

    @Operation(summary = "Tao URL thanh toan payOS", description = "Tao link thanh toan payOS cho don hang ONLINE o trang thai UNPAID")
    @PostMapping("/v1/api/user/payment/payos/{order-code}")
    public ResponseEntity<ApiRes<PaymentRes>> createPayment(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable("order-code") String orderCode,
            HttpServletRequest request) {

        String ipAddress = getClientIp(request);

        PaymentRes res = payOsService.createPaymentUrl(
                account.requireUserUuid(),
                orderCode,
                ipAddress);

        return ResponseEntity.ok(ApiRes.success("Tao link thanh toan thanh cong", res));
    }

    @Operation(summary = "payOS return URL", description = "Endpoint payOS redirect sau khi thanh toan")
    @GetMapping("/v1/api/public/payment/payos/return")
    public ResponseEntity<ApiRes<Map<String, Object>>> payOsReturn(
            @RequestParam Map<String, String> params) {

        boolean success = payOsService.processPaymentReturn(params);
        String orderCode = params.get("orderCode");

        if (success) {
            return ResponseEntity.ok(ApiRes.success("Thanh toan thanh cong",
                    Map.of("orderCode", orderCode != null ? orderCode : "",
                            "paid", true)));
        }

        return ResponseEntity.ok(ApiRes.error("Thanh toan that bai",
                Map.of("orderCode", orderCode != null ? orderCode : "",
                        "paid", false)));
    }

    @Operation(summary = "payOS cancel URL", description = "Endpoint payOS redirect khi nguoi dung huy thanh toan")
    @GetMapping("/v1/api/public/payment/payos/cancel")
    public ResponseEntity<ApiRes<Map<String, Object>>> payOsCancel(
            @RequestParam Map<String, String> params) {

        String orderCode = params.get("orderCode");
        return ResponseEntity.ok(ApiRes.error("Thanh toan da bi huy",
                Map.of("orderCode", orderCode != null ? orderCode : "",
                        "paid", false,
                        "cancelled", true)));
    }

    @Operation(summary = "payOS webhook", description = "Endpoint nhan webhook thanh toan tu payOS")
    @PostMapping("/v1/api/public/payment/payos/webhook")
    public ResponseEntity<Map<String, Object>> payOsWebhook(@RequestBody Webhook webhook) {
        payOsService.processWebhook(webhook);
        return ResponseEntity.ok(Map.of("success", true));
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
