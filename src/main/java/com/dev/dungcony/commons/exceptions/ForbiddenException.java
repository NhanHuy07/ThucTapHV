package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {
    protected ForbiddenException(String code, String message, Object data) {
        super(HttpStatus.FORBIDDEN, code, message, data);
    }

    protected ForbiddenException(String code, String message) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
