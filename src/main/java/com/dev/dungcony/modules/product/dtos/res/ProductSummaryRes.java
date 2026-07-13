package com.dev.dungcony.modules.product.dtos.res;

import java.math.BigDecimal;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;

/**
 * Lightweight projection for product listing. Matches JPQL projections in
 * repository.
 */
public record ProductSummaryRes(
        String code,
        String name,
        BigDecimal price,
        BigDecimal originalPrice,
        Float rated,
        String imgUrl,
        String categoryCode) {

    public ProductSummaryRes withDiscount(DiscountInfoDto discount) {
        if (discount == null) {
            return this;
        }
        return new ProductSummaryRes(
                code,
                name,
                discount.finalPrice(),
                discount.originalPrice(),
                rated,
                imgUrl,
                categoryCode);
    }
}
