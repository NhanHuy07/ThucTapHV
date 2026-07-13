package com.dev.dungcony.modules.cart.dtos.res;

import com.dev.dungcony.modules.product.enums.ProductSize;

import java.math.BigDecimal;

public record CartItemRes(
                String productCode,
                String productName,
                String imgUrl,
                Integer sizeId,
                ProductSize size,
                BigDecimal originalPrice,
                BigDecimal finalPrice,
                String discountType,
                int discountValue,
                Integer quantity,
                BigDecimal lineTotal) {
}
