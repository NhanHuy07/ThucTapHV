package com.dev.dungcony.modules.product.dtos.res;

import java.time.Instant;

public record CommentRes(
                String productCode,
                String content,
                Float rating,
                Instant createdAt) {

}
