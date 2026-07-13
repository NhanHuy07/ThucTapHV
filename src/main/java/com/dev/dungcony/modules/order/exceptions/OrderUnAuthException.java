package com.dev.dungcony.modules.order.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class OrderUnAuthException extends UnauthorException {
    public OrderUnAuthException() {
        super("USER_NOT_AUTH", "token lỗi");
    }
}
