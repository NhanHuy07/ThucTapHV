package com.dev.dungcony.modules.promotion.services.impl;

import com.dev.dungcony.modules.product.services.interfaces.GetIdByCode;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;
import com.dev.dungcony.modules.promotion.entities.PromotionProduct;
import com.dev.dungcony.modules.promotion.entities.PromotionProductId;
import com.dev.dungcony.modules.promotion.enums.PromotionStatus;
import com.dev.dungcony.modules.promotion.repositories.PromotionProductRepository;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionProductService;

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
public class PromotionProductServiceImpl implements PromotionProductService {
    private final PromotionProductRepository promotionProductRepository;
    private final GetIdByCode getIdByCode;

    @Override
    public List<PromotionSummaryRes> getPromotionByProduct(String code) {
        return promotionProductRepository.findByProductId(getIdByCode.getByProductCode(code), Instant.now(),
                PromotionStatus.ACTIVE);
    }

    @Override
    public Map<String, List<PromotionSummaryRes>> getPromotionsByProducts(List<String> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> codeToId = getIdByCode.mapProductCodesToIds(productCodes);
        List<Integer> productIds = codeToId.values().stream().toList();

        List<Object[]> rows = promotionProductRepository
                .findByProductIds(productIds, Instant.now(), PromotionStatus.ACTIVE);

        Map<Integer, String> idToCode = codeToId.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        return rows.stream()
                .collect(Collectors.groupingBy(
                        row -> idToCode.get((Integer) row[0]),
                        Collectors.mapping(row -> (PromotionSummaryRes) row[1], Collectors.toList())));
    }

    @Transactional
    @Override
    public void addListPromotionProduct(Promotion pro, List<String> productCodes) {
        log.info("Adding {} promotion-product mappings for promotionId={}", productCodes.size(), pro.getId());

        List<PromotionProduct> mappings = productCodes.stream()
                .map(productCode -> {
                    PromotionProduct pp = new PromotionProduct();
                    pp.setPromotion(pro);
                    pp.setId(new PromotionProductId(getIdByCode.getByProductCode(productCode), pro.getId()));
                    return pp;
                })
                .toList();

        promotionProductRepository.saveAll(mappings);
    }
}
