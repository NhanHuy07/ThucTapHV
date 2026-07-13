package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class ProductConflictException extends ConflictException {
    public ProductConflictException() {
        super("product is already existed");
    }

    public ProductConflictException(String mes) {
        super(mes);
    }

    public ProductConflictException(String code, String message) {
        super(code, message);
    }
}
