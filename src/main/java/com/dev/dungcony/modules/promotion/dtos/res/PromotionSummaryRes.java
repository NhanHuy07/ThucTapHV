package com.dev.dungcony.modules.promotion.dtos.res;

import java.math.BigDecimal;
import java.time.Instant;

import com.dev.dungcony.modules.promotion.enums.PromotionType;

public record PromotionSummaryRes(
        Integer id,
        int value,
        Instant startAt,
        Instant endAt) {
    private static final BigDecimal FIXED_DISCOUNT_RATIO_LIMIT = new BigDecimal("0.30");

    public PromotionType promotionType() {
        return value < 100 ? PromotionType.PERCENT : PromotionType.FIXED;
    }

    /**
     * Kiểm tra promotion có áp dụng được cho giá sản phẩm và thời điểm hiện tại
     * không.
     * Sử dụng timestamp truyền vào thay vì gọi Instant.now() nhiều lần để đảm bảo
     * tính nhất quán.
     */
    public boolean isApplicable(BigDecimal price, Instant now) {
        if (price == null || now == null || startAt == null || endAt == null) {
            return false;
        }

        if (now.isBefore(startAt) || !now.isBefore(endAt)) {
            return false;
        }

        if (promotionType() == PromotionType.PERCENT) {
            return true;
        }

        BigDecimal maxAllowedDiscount = price.multiply(FIXED_DISCOUNT_RATIO_LIMIT);
        return calculateDiscount(price).compareTo(maxAllowedDiscount) < 0;
    }

    /**
     * Tính số tiền được giảm dựa trên loại promotion.
     */
    public BigDecimal calculateDiscount(BigDecimal price) {
        return switch (promotionType()) {
            case PERCENT -> price.multiply(BigDecimal.valueOf(value))
                    .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
            case FIXED -> BigDecimal.valueOf(value).min(price); // Không giảm quá giá sản phẩm
        };
    }
}
