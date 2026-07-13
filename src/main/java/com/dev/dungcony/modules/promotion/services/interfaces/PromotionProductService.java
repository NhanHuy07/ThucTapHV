package com.dev.dungcony.modules.promotion.services.interfaces;

import java.util.List;
import java.util.Map;

import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;

public interface PromotionProductService {
    List<PromotionSummaryRes> getPromotionByProduct(String productCode);

    /**
     * Batch: lấy promotions cho nhiều products cùng lúc.
     * Key = productCode, Value = danh sách PromotionDto
     */
    Map<String, List<PromotionSummaryRes>> getPromotionsByProducts(List<String> productCodes);

    void addListPromotionProduct(Promotion promotion, List<String> productCodes);
}
