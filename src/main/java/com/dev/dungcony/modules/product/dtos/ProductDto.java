package com.dev.dungcony.modules.product.dtos;

import com.dev.dungcony.modules.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductDto(
        Integer id,
        String name,
        String productCode,
        String description,

        BigDecimal originalPrice,
        BigDecimal finalPrice,
        String discountType,
        int discountValue,

        Integer sold,

        Float rating,

        String imgUrl,
        ProductStatus status,
        List<ItemDto> items,

        Instant createdAt,
        Instant updatedAt
) {
}
