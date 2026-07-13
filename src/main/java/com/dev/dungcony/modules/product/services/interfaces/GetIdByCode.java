package com.dev.dungcony.modules.product.services.interfaces;

import java.util.List;
import java.util.Map;

public interface GetIdByCode {
    List<Integer> getByCategoryCodes(List<String> codes);

    Integer getByCategoryCode(String code);

    List<Integer> getByProductCodes(List<String> codes);

    Integer getByProductCode(String code);

    Map<String, Integer> mapProductCodesToIds(List<String> codes);

    Map<String, Integer> mapCategoryCodesToIds(List<String> codes);
}
