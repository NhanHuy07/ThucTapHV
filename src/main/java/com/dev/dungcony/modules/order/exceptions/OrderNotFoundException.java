package com.dev.dungcony.modules.order.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends AppException {

    public OrderNotFoundException() {
        super(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found");
    }

    public OrderNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", message);
    }
}
