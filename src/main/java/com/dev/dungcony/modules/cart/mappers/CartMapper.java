package com.dev.dungcony.modules.cart.mappers;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;
import com.dev.dungcony.modules.cart.dtos.CartItemDto;
import com.dev.dungcony.modules.cart.dtos.res.CartItemRes;
import com.dev.dungcony.modules.cart.dtos.res.CartRes;
import com.dev.dungcony.modules.cart.entities.CartItem;
import com.dev.dungcony.modules.product.enums.ProductSize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CartMapper {

    public static CartItemRes toCartItemRes(CartItem item, DiscountInfoDto discount) {
        BigDecimal finalPrice = discount.finalPrice();
        BigDecimal lineTotal = finalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemRes(
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getProduct().getImg(),
                item.getSize().getId(),
                item.getSize().getSize(),
                discount.originalPrice(),
                discount.finalPrice(),
                discount.discountType(),
                discount.discountValue(),
                item.getQuantity(),
                lineTotal);
    }

    public static CartRes toCartRes(List<CartItem> items, Map<String, DiscountInfoDto> discountMap) {
        List<CartItemRes> itemResList = items.stream()
                .map(item -> {
                    DiscountInfoDto discount = discountMap.getOrDefault(
                            item.getProduct().getCode(),
                            DiscountInfoDto.noDiscount(item.getProduct().getPrice()));
                    return toCartItemRes(item, discount);
                })
                .toList();

        BigDecimal totalAmount = itemResList.stream()
                .map(CartItemRes::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartRes(itemResList, itemResList.size(), totalAmount);
    }

    public static CartItemDto toDto(int productId, String productCode, ProductSize productSize) {
        return new CartItemDto(
                productId,
                productCode,
                productSize,
                null, null, null, null
        );
    }
}
