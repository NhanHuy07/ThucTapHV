package com.dev.dungcony.modules.order.dtos.req;

import com.dev.dungcony.modules.order.enums.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusReq(
        @NotBlank String orderCode,
        @NotNull OrderStatus status) {
}
