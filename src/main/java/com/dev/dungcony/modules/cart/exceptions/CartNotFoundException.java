package com.dev.dungcony.modules.cart.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class CartNotFoundException extends NotFoundException {

    public CartNotFoundException() {
        super("CART_NOT_FOUND", "Cart not found");
    }

}
