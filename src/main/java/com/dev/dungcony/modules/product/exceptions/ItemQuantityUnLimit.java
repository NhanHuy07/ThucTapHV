package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class ItemQuantityUnLimit extends ConflictException {
    public ItemQuantityUnLimit() {
        super("too many items");
    }
}
