package com.dev.dungcony.modules.promotion.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromotionType {
    PERCENT("PERCENT"),
    FIXED("FIXED");

    @JsonValue
    private final String value;
}
