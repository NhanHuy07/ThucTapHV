package com.dev.dungcony.modules.product.dtos.req;

import com.dev.dungcony.modules.product.enums.CategoryStatus;

public record CategoryUpdateReq(
                String name,
                String description,
                String imgUrl,
                CategoryStatus status) {
}