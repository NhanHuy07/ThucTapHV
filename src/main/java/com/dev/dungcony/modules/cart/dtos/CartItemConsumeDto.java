package com.dev.dungcony.modules.cart.dtos;

import com.dev.dungcony.modules.product.enums.ProductSize;

public record CartItemConsumeDto(
        Integer productId,
        ProductSize productSize,
        int quantity
) {
}
