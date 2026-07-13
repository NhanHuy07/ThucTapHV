package com.dev.dungcony.modules.product.services.interfaces.category;

import java.util.List;

import com.dev.dungcony.modules.product.dtos.res.CategoryRes;

public interface CategoryGetService {
    List<CategoryRes> getAllChildren(String path);

    List<CategoryRes> getAll();

    List<CategoryRes> getAllLeaf();

    CategoryRes getByCode(String code);

    CategoryRes getByName(String name);

    long coutByCodes(List<String> codes);
}
