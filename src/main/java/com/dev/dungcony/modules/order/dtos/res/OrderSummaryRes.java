package com.dev.dungcony.modules.order.dtos.res;

import java.math.BigDecimal;
import java.time.Instant;

import com.dev.dungcony.modules.order.enums.OrderStatus;

public record OrderSummaryRes(
        String orderCode,
        OrderStatus status,
        BigDecimal finalPrice,
        int totalItems,
        Instant createdAt) {
}
