package com.dev.dungcony.modules.product.dtos.req;

import java.math.BigDecimal;

public record ProductUpdateReq(
                String name,
                String description,
                BigDecimal price,
                Integer sold,
                Float rate,
                String videoUrl,
                String imgUrl) {
}