package com.dev.dungcony.commons.dtos;

import java.math.BigDecimal;

public record DiscountInfoDto(
        BigDecimal originalPrice,
        BigDecimal finalPrice,
        String discountType,
        int discountValue) {
    public static DiscountInfoDto noDiscount(BigDecimal price) {
        return new DiscountInfoDto(price, price, "NONE", 0);
    }
}
