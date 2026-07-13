package com.dev.dungcony.modules.cart.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class CartUnProcessableException extends UnProcessableException {
    public CartUnProcessableException() {
        super("ERROR_INPUT", "có lỗi khi cập nhật giỏ hàng");
    }
}
