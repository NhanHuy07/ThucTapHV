package com.dev.dungcony.modules.order.dtos;

import com.dev.dungcony.modules.product.enums.ProductSize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderItemDto(
        String productCode,
        ProductSize size,
        @Size(min = 0) int quantity,
        @NotNull(message = "Giá sản phẩm phải có") BigDecimal originalPrice,
        BigDecimal finalPrice
) {
}
