package com.dev.dungcony.modules.product.services.interfaces.comment;

import java.util.UUID;

import com.dev.dungcony.modules.product.dtos.req.CommentAddReq;
import com.dev.dungcony.modules.product.dtos.res.CommentRes;

public interface CommentUpdateService {
    CommentRes updateMyComment(UUID uid, CommentAddReq req);
}
