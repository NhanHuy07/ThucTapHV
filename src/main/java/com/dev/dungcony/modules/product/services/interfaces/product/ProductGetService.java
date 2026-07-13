package com.dev.dungcony.modules.product.services.interfaces.product;

import com.dev.dungcony.modules.product.dtos.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductGetService {
    Integer getIdByCode(String code);

    ProductSummaryRes getById(int id);

    ProductDto getDtoByCode(String code);

    Map<String, ProductDto> getDtoByCodes(List<String> codes);

    ProductDetailRes getDetailByCode(String code);

    Page<ProductSummaryRes> getAll(Pageable pageable);

    Page<ProductSummaryRes> getAllByCategoryCode(String categoryCode, Pageable pageable);

    Page<ProductSummaryRes> searchByKeyword(String keyword, Pageable pageable);

    Page<ProductSummaryRes> filter(String categoryCode, BigDecimal minPrice, BigDecimal maxPrice,
                                   String keyword, Pageable pageable);

    Page<ProductSummaryRes> getAllBestSeller(Pageable pageable);

    long countByCodes(List<String> codes);
}
