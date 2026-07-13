package com.dev.dungcony.modules.users.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankType {
    Bronze,
    Silver,
    Gold,
    Platinum,
    Diamond,
    Ultimate
}
