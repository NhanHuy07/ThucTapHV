package com.dev.dungcony.modules.order.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class OrderCannotCreateException extends UnProcessableException {
    public OrderCannotCreateException() {
        super("NOT_ITEM", "cần ít nhất 1 sp");
    }

    public OrderCannotCreateException(String code, String message) {
        super(code, message);
    }

}
