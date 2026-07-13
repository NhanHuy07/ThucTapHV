package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.InvalidException;

public class CommentInvalidException extends InvalidException {

    public CommentInvalidException(String message) {
        super("COMMENT_INVALID", message);
    }
}
