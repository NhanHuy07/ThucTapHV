package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException() {
        super("CATEGORY_NOT_FOUND", "category not found");
    }
}
