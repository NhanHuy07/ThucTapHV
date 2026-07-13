package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class CategoryConflict extends ConflictException {

    public CategoryConflict(String mes) {
        super(mes);
    }
}
