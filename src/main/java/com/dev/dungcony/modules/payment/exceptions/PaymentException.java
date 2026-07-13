package com.dev.dungcony.modules.payment.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class PaymentException extends AppException {
    public PaymentException(String message) {
        super(HttpStatus.BAD_REQUEST, "PAYMENT_ERROR", message);
    }

    public PaymentException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
