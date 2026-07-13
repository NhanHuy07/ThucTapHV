package com.dev.dungcony.modules.product.services.impl;

import com.dev.dungcony.modules.product.repositories.CategoryRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.GetIdByCode;
import com.dev.dungcony.modules.promotion.exceptions.PromotionUnProcessable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetIdByCodeImpl implements GetIdByCode {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Integer> getByCategoryCodes(List<String> codes) {
        return mapCategoryCodesToIds(codes).values().stream().toList();
    }

    @Override
    public Integer getByCategoryCode(String code) {
        return categoryRepository.findByCode(code)
                .orElseThrow(() -> new PromotionUnProcessable("Category code not found: " + code))
                .getId();
    }

    @Override
    public List<Integer> getByProductCodes(List<String> codes) {
        return mapProductCodesToIds(codes).values().stream().toList();
    }

    @Override
    public Integer getByProductCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new PromotionUnProcessable("Product code not found: " + code))
                .getId();
    }

    @Override
    public Map<String, Integer> mapProductCodesToIds(List<String> codes) {
        return toMap(codes, this::getByProductCode);
    }

    @Override
    public Map<String, Integer> mapCategoryCodesToIds(List<String> codes) {
        return toMap(codes, this::getByCategoryCode);
    }

    private Map<String, Integer> toMap(List<String> codes, Function<String, Integer> mapper) {
        if (codes == null) {
            throw new PromotionUnProcessable("Codes must not be null");
        }
        return codes.stream()
                .collect(Collectors.toMap(Function.identity(), mapper, (a, b) -> a));
    }
}
