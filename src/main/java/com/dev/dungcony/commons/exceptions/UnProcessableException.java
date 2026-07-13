package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class UnProcessableException extends AppException {
    protected UnProcessableException(String code, String message, Object data) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, code, message, data);
    }

    protected UnProcessableException(String code, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, code, message);
    }
}
