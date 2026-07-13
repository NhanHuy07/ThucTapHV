package com.dev.dungcony.modules.promotion.services.impl;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCalculator;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCategoryService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionProductService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Service chuyên trách tính toán giá sau promotion.
 * Tách riêng khỏi PromotionServiceImpl để tuân thủ SRP (Single Responsibility
 * Principle).
 * <p>
 * Delegate query xuống service layer (PromotionProductService,
 * PromotionCategoryService, PromotionService)
 * thay vì gọi trực tiếp repository — đảm bảo single source of truth.
 * <p>
 * Hỗ trợ cả tính đơn lẻ (1 product) và batch (nhiều products) để tránh N+1
 * query.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionCalculatorImpl implements PromotionCalculator {

        private final PromotionProductService promotionProductService;
        private final PromotionCategoryService promotionCategoryService;
        private final PromotionService promotionService;

        @Override
        public DiscountInfoDto calculateFinalPrice(String productCode, String categoryCode, BigDecimal price) {
                log.debug("Calculating final price for productCode={}, categoryCode={}, price={}", productCode,
                                categoryCode, price);

                Instant now = Instant.now();

                // Delegate sang service layer — single source of truth
                List<PromotionSummaryRes> productPromotions = promotionProductService
                                .getPromotionByProduct(productCode);
                List<PromotionSummaryRes> categoryPromotions = promotionCategoryService
                                .getPromotionByCategory(categoryCode);
                List<PromotionSummaryRes> globalPromotions = promotionService.getGlobalPromotions(now);

                return findBestDiscount(price, now, productPromotions, categoryPromotions, globalPromotions);
        }

        @Override
        public Map<String, DiscountInfoDto> calculateFinalPrices(List<ProductPriceInput> inputs) {
                if (inputs == null || inputs.isEmpty()) {
                        return Collections.emptyMap();
                }

                Instant now = Instant.now();

                List<String> productCodes = inputs.stream().map(ProductPriceInput::productCode).toList();
                List<String> categoryCodes = inputs.stream()
                                .map(ProductPriceInput::categoryCode)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();

                Map<String, List<PromotionSummaryRes>> productPromotionMap = promotionProductService
                                .getPromotionsByProducts(productCodes);
                Map<String, List<PromotionSummaryRes>> categoryPromotionMap = promotionCategoryService
                                .getPromotionsByCategories(categoryCodes);
                List<PromotionSummaryRes> globalPromotions = promotionService.getGlobalPromotions(now);

                Map<String, DiscountInfoDto> result = new HashMap<>();
                for (ProductPriceInput input : inputs) {
                        List<PromotionSummaryRes> prodPromos = productPromotionMap.getOrDefault(input.productCode(),
                                        List.of());
                        List<PromotionSummaryRes> catePromos = input.categoryCode() != null
                                        ? categoryPromotionMap.getOrDefault(input.categoryCode(), List.of())
                                        : List.of();

                        DiscountInfoDto discount = findBestDiscount(input.price(), now, prodPromos, catePromos,
                                        globalPromotions);
                        result.put(input.productCode(), discount);
                }

                return result;
        }

        // ============ PRIVATE HELPERS ============

        /**
         * Tìm promotion tốt nhất (giảm nhiều nhất) từ tất cả các nguồn.
         */
        private DiscountInfoDto findBestDiscount(
                        BigDecimal price,
                        Instant now,
                        List<PromotionSummaryRes> productPromotions,
                        List<PromotionSummaryRes> categoryPromotions,
                        List<PromotionSummaryRes> globalPromotions) {
                List<PromotionSummaryRes> allPromotions = new ArrayList<>(
                                productPromotions.size() + categoryPromotions.size() + globalPromotions.size());
                allPromotions.addAll(productPromotions);
                allPromotions.addAll(categoryPromotions);
                allPromotions.addAll(globalPromotions);

                List<PromotionSummaryRes> applicablePromotions = allPromotions.stream()
                                .filter(promo -> promo.isApplicable(price, now))
                                .toList();

                if (applicablePromotions.isEmpty()) {
                        return DiscountInfoDto.noDiscount(price);
                }

                // Tìm promotion cho discount cao nhất
                PromotionSummaryRes bestPromotion = applicablePromotions.stream()
                                .max(Comparator.comparing(p -> p.calculateDiscount(price)))
                                .orElseThrow();

                BigDecimal discount = bestPromotion.calculateDiscount(price);
                BigDecimal finalPrice = price.subtract(discount);

                log.debug("Best promotion: type={}, value={}, discount={}, finalPrice={}",
                                bestPromotion.promotionType(), bestPromotion.value(), discount, finalPrice);

                return new DiscountInfoDto(price, finalPrice, bestPromotion.promotionType().getValue(),
                                bestPromotion.value());
        }
}
