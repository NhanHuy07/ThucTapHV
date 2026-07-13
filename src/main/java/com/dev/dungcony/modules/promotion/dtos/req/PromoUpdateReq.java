package com.dev.dungcony.modules.promotion.dtos.req;

import jakarta.validation.constraints.Min;

import java.time.Instant;

import com.dev.dungcony.modules.promotion.enums.PromotionStatus;

public record PromoUpdateReq(
        Integer id,
        @Min(value = 0, message = "Value must be non-negative") Integer value,
        Instant startAt,
        Instant endAt,
        @Min(value = 0, message = "Priority must be non-negative") Integer priority,
        PromotionStatus status) {
}
