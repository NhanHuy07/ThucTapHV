package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ForbiddenException;

public class CommentForbiddenException extends ForbiddenException {

    public CommentForbiddenException(String message) {
        super("COMMENT_FORBIDDEN", message);
    }
}
