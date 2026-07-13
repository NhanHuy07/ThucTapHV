package com.dev.dungcony.commons.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends AppException {
    protected ConflictException(String mes) {
        super(HttpStatus.CONFLICT, "Conflict", mes);
    }

    protected ConflictException(String code, String mes) {
        super(HttpStatus.CONFLICT, code, mes);
    }
}
