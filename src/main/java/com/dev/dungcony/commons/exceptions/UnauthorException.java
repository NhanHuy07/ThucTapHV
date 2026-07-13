package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorException extends AppException {
    protected UnauthorException(String code, String message, Object data) {
        super(HttpStatus.UNAUTHORIZED, code, message, data);
    }

    protected UnauthorException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
