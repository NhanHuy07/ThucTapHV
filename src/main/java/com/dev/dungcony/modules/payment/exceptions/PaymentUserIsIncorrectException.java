package com.dev.dungcony.modules.payment.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class PaymentUserIsIncorrectException extends UnauthorException {
    public PaymentUserIsIncorrectException() {
        super("USER_IS_INCORRECT", "user có lẽ bị sai");
    }
}
