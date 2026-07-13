package com.dev.dungcony.modules.cart.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException() {
        super("CART_ITEM_NOT_FOUND", "Cart item not found");
    }

    public CartItemNotFoundException(String message) {
        super("CART_ITEM_NOT_FOUND", message);
    }
}
