package com.dev.dungcony.modules.cart.services.impl;

import com.dev.dungcony.modules.cart.repositories.CartRepository;
import com.dev.dungcony.modules.cart.services.interfaces.CartItemGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemGetImpl implements CartItemGetService {

    private final CartRepository cartRepository;

    @Override
    public boolean existsLitProductCode(UUID uid, List<String> productCodes) {
        long count = cartRepository.countDistinctProductCodesByUserIdAndCodes(uid, productCodes);
        return count == productCodes.size();
    }
}
