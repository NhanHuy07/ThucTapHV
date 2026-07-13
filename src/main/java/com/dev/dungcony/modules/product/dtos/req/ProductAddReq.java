package com.dev.dungcony.modules.product.dtos.req;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductAddReq(
        @NotNull String categoryCode,
        @NotNull String providerCode,
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        String imgUrl) {
}