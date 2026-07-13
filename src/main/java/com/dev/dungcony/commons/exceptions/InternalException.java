package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class InternalException extends AppException {
    protected InternalException(String code, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, code, message);
    }
}
