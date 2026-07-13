package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends AppException {

    public ProductNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", message);
    }

    public ProductNotFoundException() {
        super(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "not found product");
    }
}
