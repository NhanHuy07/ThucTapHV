package com.dev.dungcony.modules.product.dtos;

import com.dev.dungcony.modules.product.enums.ItemStatus;
import com.dev.dungcony.modules.product.enums.ProductSize;

public record ItemDto(
        String productCode,
        ProductSize size,
        ItemStatus status,
        Integer quantity) {
}
