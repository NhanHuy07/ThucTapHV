package com.dev.dungcony.modules.product.dtos.req;

public record CommentAddReq(
        String productCode,
        String content,
        Float rating) {
}