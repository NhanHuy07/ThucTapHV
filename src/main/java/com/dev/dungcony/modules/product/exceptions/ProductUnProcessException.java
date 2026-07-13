package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class ProductUnProcessException extends UnProcessableException {
    public ProductUnProcessException() {
        super("error", "số lượng sản phẩm trả về không hợp lệ");
    }
}
