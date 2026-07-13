package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class ItemNotFoundException extends NotFoundException {

    public ItemNotFoundException() {
        super("not found item");
    }
}
