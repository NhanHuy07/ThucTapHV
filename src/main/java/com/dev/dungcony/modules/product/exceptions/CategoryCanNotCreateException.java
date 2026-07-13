package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class CategoryCanNotCreateException extends AppException {
    public CategoryCanNotCreateException(String message) {
        super(HttpStatus.BAD_REQUEST, "not created", message);
    }
}
