package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class ProviderConflictException extends ConflictException {
    public ProviderConflictException(String mes) {
        super("CONFLICT", mes);
    }

    public ProviderConflictException(String code, String mes) {
        super(code, mes);
    }

    public ProviderConflictException() {
        super("Provider is already exist");
    }
}
