package com.dev.dungcony.modules.promotion.services.interfaces;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PromotionCalculator {

    DiscountInfoDto calculateFinalPrice(String productCode, String categoryCode, BigDecimal price);

    Map<String, DiscountInfoDto> calculateFinalPrices(List<ProductPriceInput> inputs);

    record ProductPriceInput(String productCode, String categoryCode, BigDecimal price) {
    }
}
