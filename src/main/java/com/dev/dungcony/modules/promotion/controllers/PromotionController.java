package com.dev.dungcony.modules.promotion.controllers;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionDetailRes;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCategoryService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionProductService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Promotions")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/promotions")
public class PromotionController {

        private final PromotionService promotionService;
        private final PromotionProductService promotionProductService;
        private final PromotionCategoryService promotionCategoryService;

        @GetMapping("/product/{productCode}")
        public ResponseEntity<ApiRes<List<PromotionSummaryRes>>> getPromotionsByProduct(
                        @PathVariable String productCode) {
                List<PromotionSummaryRes> promotions = promotionProductService.getPromotionByProduct(productCode);
                return ResponseEntity.ok()
                                .body(ApiRes.success("Promotions for product", promotions));
        }

        @GetMapping("/category/{categoryCode}")
        public ResponseEntity<ApiRes<List<PromotionSummaryRes>>> getPromotionsByCategory(
                        @PathVariable String categoryCode) {
                List<PromotionSummaryRes> promotions = promotionCategoryService.getPromotionByCategory(categoryCode);
                return ResponseEntity.ok()
                                .body(ApiRes.success("Promotions for category", promotions));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiRes<PromotionDetailRes>> getById(
                        @PathVariable Integer id) {
                return promotionService.getById(id)
                                .map(promotion -> ResponseEntity.ok()
                                                .body(ApiRes.success("Promotion detail", promotion)))
                                .orElse(ResponseEntity.notFound().build());
        }

}
