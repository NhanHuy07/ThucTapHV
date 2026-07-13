package com.dev.dungcony.modules.cart.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class NotAddOutOfStockProduct extends UnProcessableException {

    public NotAddOutOfStockProduct() {
        super("NOT_ADD", "không thể thêm sản phẩm hết hàng vào cart");
    }
}
