package com.dev.dungcony.modules.product.dtos.res;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.dev.dungcony.modules.product.dtos.CategorySummaryDto;
import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.dtos.ProviderSummaryDto;
import com.dev.dungcony.modules.product.enums.ProductStatus;

public record ProductDetailRes(
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
        Instant updatedAt,
        CategorySummaryDto category,
        ProviderSummaryDto provider) {
}