package com.dev.dungcony.modules.cart.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class CartAlreadyExistsException extends ConflictException {

    public CartAlreadyExistsException() {
        super("Cart already exists");
    }

}
