package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidException extends AppException {
    protected InvalidException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }

    protected InvalidException(String code, String message, Object data) {
        super(HttpStatus.BAD_REQUEST, code, message, data);
    }
}
