package com.dev.dungcony.modules.promotion.services.impl;

import com.dev.dungcony.modules.product.services.interfaces.GetIdByCode;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;
import com.dev.dungcony.modules.promotion.entities.PromotionCategory;
import com.dev.dungcony.modules.promotion.entities.PromotionCategoryId;
import com.dev.dungcony.modules.promotion.enums.PromotionStatus;
import com.dev.dungcony.modules.promotion.repositories.PromotionCategoryRepository;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCategoryService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionCategoryServiceImpl implements PromotionCategoryService {

    private final PromotionCategoryRepository promotionCategoryRepository;
    private final GetIdByCode getIdByCode;

    @Transactional
    @Override
    public void addListPromotionCategory(Promotion promotion, List<String> categoryCodes) {
        log.info("Adding {} promotion-category mappings for promotionId={}", categoryCodes.size(), promotion.getId());

        List<PromotionCategory> mappings = categoryCodes.stream()
                .map(categoryCode -> {
                    PromotionCategory pp = new PromotionCategory();
                    pp.setPromotion(promotion);
                    pp.setId(new PromotionCategoryId(getIdByCode.getByCategoryCode(categoryCode), promotion.getId()));
                    return pp;
                })
                .toList();

        promotionCategoryRepository.saveAll(mappings);
    }

    @Override
    public List<PromotionSummaryRes> getPromotionByCategory(String categoryCode) {
        return promotionCategoryRepository.findByCategoryId(getIdByCode.getByCategoryCode(categoryCode), Instant.now(),
                PromotionStatus.ACTIVE);
    }

    @Override
    public Map<String, List<PromotionSummaryRes>> getPromotionsByCategories(List<String> categoryCodes) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> codeToId = getIdByCode.mapCategoryCodesToIds(categoryCodes);
        List<Integer> categoryIds = codeToId.values().stream().toList();

        List<Object[]> rows = promotionCategoryRepository
                .findByCategoryIds(categoryIds, Instant.now(), PromotionStatus.ACTIVE);

        Map<Integer, String> idToCode = codeToId.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        return rows.stream()
                .collect(Collectors.groupingBy(
                        row -> idToCode.get((Integer) row[0]),
                        Collectors.mapping(row -> (PromotionSummaryRes) row[1], Collectors.toList())));
    }
}
