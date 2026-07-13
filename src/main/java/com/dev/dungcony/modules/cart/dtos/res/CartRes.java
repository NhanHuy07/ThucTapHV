package com.dev.dungcony.modules.cart.dtos.res;

import java.math.BigDecimal;
import java.util.List;

public record CartRes(
                List<CartItemRes> items,
                Integer totalItems,
                BigDecimal totalAmount) {
}
