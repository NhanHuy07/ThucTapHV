package com.dev.dungcony.modules.promotion.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromotionStatus {
    SCHEDULED("SCHEDULED"),
    ACTIVE("ACTIVE"),
    ENDED("ENDED"),
    DELETED("DELETED");

    @JsonValue
    private final String value;

}
