package com.dev.dungcony.modules.cart.dtos.req;

import com.dev.dungcony.modules.cart.dtos.CartItemDto;

import java.util.List;

public record RemoveListItemReq(
        List<CartItemDto> listRemove
) {
}
