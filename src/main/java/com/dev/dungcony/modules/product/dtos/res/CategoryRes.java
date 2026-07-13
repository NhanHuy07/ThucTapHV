package com.dev.dungcony.modules.product.dtos.res;

import com.dev.dungcony.modules.product.enums.CategoryStatus;

public record CategoryRes(
                String name,
                String categoryCode,
                CategoryStatus status,
                String description,
                String imgUrl) {

}
