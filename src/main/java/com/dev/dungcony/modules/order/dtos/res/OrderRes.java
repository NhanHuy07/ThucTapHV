package com.dev.dungcony.modules.order.dtos.res;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.dev.dungcony.modules.order.dtos.OrderItemDto;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;

public record OrderRes(
        String orderCode,
        OrderStatus status,
        PaymentType paymentType,
        String voucherCode,
        ReceiverRes reciever,
        String note,
        List<OrderItemDto> items,
        BigDecimal totalAmount,
        BigDecimal voucherDiscount,
        BigDecimal finalPrice,
        String paymentUrl,
        Instant createdAt,
        Instant updatedAt) {
}
