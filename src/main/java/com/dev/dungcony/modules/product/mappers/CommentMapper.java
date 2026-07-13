package com.dev.dungcony.modules.product.mappers;

import com.dev.dungcony.modules.product.dtos.res.CommentRes;
import com.dev.dungcony.modules.product.entities.Comment;

public class CommentMapper {

    public static CommentRes toCommentRes(Comment comment) {
        return new CommentRes(
                comment.getProduct().getCode(),
                comment.getContent(),
                comment.getRating(),
                comment.getCreatedAt());
    }
}
