package com.dev.dungcony.modules.cart.services.impl;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;
import com.dev.dungcony.modules.cart.dtos.res.CartRes;
import com.dev.dungcony.modules.cart.entities.CartItem;
import com.dev.dungcony.modules.cart.mappers.CartMapper;
import com.dev.dungcony.modules.cart.repositories.CartRepository;
import com.dev.dungcony.modules.cart.services.interfaces.CartGetService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCalculator;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCalculator.ProductPriceInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class CartGetImpl implements CartGetService {

    private final CartRepository cartRepository;
    private final PromotionCalculator promotionCalculator;

    @Override
    @Transactional(readOnly = true)
    public CartRes getCart(UUID userId) {
        List<CartItem> items = cartRepository.findAllByUserId(userId);

        if (items.isEmpty()) {
            return CartMapper.toCartRes(items, Map.of());
        }

        List<ProductPriceInput> inputs = items.stream()
                .map(ci -> new ProductPriceInput(
                        ci.getProduct().getCode(),
                        ci.getProduct().getCategory() != null
                                ? ci.getProduct().getCategory().getCode() : null,
                        ci.getProduct().getPrice()))
                .distinct()
                .toList();

        Map<String, DiscountInfoDto> discountMap = promotionCalculator.calculateFinalPrices(inputs);

        return CartMapper.toCartRes(items, discountMap);
    }
    
}
