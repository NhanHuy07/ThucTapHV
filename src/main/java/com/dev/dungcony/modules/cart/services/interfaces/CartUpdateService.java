package com.dev.dungcony.modules.cart.services.interfaces;

import com.dev.dungcony.modules.cart.dtos.CartItemDto;
import com.dev.dungcony.modules.cart.dtos.CartItemConsumeDto;
import com.dev.dungcony.modules.cart.dtos.req.AddToCartReq;
import com.dev.dungcony.modules.cart.dtos.req.UpdateCartItemReq;
import com.dev.dungcony.modules.product.enums.ProductSize;

import java.util.List;
import java.util.UUID;

public interface CartUpdateService {
    void addItemToCart(UUID userId, AddToCartReq req);

    void removeItemFromCart(UUID userId, String productCode, ProductSize size);

    void updateItemQuantity(UUID userId, UpdateCartItemReq req);

    void removeListItem(UUID uid, List<CartItemDto> cartItemDtos);

    void consumeListItem(UUID uid, List<CartItemConsumeDto> cartItemDtos);

    void clearCart(UUID userId);
}
