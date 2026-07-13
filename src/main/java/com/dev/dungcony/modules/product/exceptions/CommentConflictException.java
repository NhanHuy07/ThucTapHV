package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class CommentConflictException extends ConflictException {

    public CommentConflictException(String message) {
        super("COMMENT_CONFLICT", message);
    }
}
