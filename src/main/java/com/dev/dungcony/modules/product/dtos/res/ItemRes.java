package com.dev.dungcony.modules.product.dtos.res;

import com.dev.dungcony.modules.product.enums.ProductSize;

import java.math.BigDecimal;

public record ItemRes(
        String code,
        String name,
        BigDecimal price,
        Float rated,
        String imgUrl,
        ProductSize size
) {
}
