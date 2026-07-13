package com.dev.dungcony.modules.cart.services.interfaces;

import com.dev.dungcony.modules.cart.dtos.res.CartRes;

import java.util.UUID;

public interface CartGetService {
    CartRes getCart(UUID userId);

}
