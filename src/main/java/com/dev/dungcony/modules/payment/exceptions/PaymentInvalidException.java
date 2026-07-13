package com.dev.dungcony.modules.payment.exceptions;

import com.dev.dungcony.commons.exceptions.InvalidException;

public class PaymentInvalidException extends InvalidException {

    public PaymentInvalidException() {
        super("INVALID_PAYMENT_TYPE", "đơn hàng không phải kiểu thanh toán online");
    }
}
