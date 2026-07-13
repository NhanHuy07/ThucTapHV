package com.dev.dungcony.modules.promotion.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

import com.dev.dungcony.modules.promotion.enums.PromotionScope;

public record PromoAddReq(
        @NotNull(message = "Promotion value is required") @Min(value = 0, message = "Value must be non-negative") Integer value,
        @NotNull(message = "Promotion scope is required") PromotionScope scope,
        @NotNull(message = "Start date is required") Instant startAt,
        @NotNull(message = "End date is required") Instant endAt,
        @Min(value = 0, message = "Priority must be non-negative") Integer priority,
        List<String> productCodes,
        List<String> categoryCodes) {
}
