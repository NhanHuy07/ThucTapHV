package com.dev.dungcony.modules.promotion.services.interfaces;

import java.util.List;
import java.util.Map;

import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;

public interface PromotionCategoryService {
    void addListPromotionCategory(Promotion promotion, List<String> codes);

    List<PromotionSummaryRes> getPromotionByCategory(String code);

    /**
     * Batch: lấy promotions cho nhiều categories cùng lúc.
     * Key = categoryCode, Value = danh sách PromotionDto
     */
    Map<String, List<PromotionSummaryRes>> getPromotionsByCategories(List<String> codes);
}
