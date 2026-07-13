package com.dev.dungcony.modules.order.dtos;

import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        int id,
        UUID uid,
        String orderCode,
        OrderStatus status,
        PaymentType paymentType,
        List<OrderItemDto> items,
        BigDecimal totalPrice,
        BigDecimal voucherDiscount,
        BigDecimal finalPrice,
        String paymentUrl,
        Instant createAt
) {
}
