package com.dev.dungcony.modules.product.services.interfaces.comment;

import java.util.UUID;

import com.dev.dungcony.modules.product.dtos.req.CommentAddReq;

public interface CommentCreateService {
    String createComment(UUID uid, CommentAddReq req);
}
