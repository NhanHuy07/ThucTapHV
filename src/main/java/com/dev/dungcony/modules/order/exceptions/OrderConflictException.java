package com.dev.dungcony.modules.order.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class OrderConflictException extends ConflictException {

    public OrderConflictException() {
        super("Order conflict");
    }

    public OrderConflictException(String message) {
        super(message);
    }
}
