package com.dev.dungcony.modules.product.services.interfaces.category;

import com.dev.dungcony.modules.product.dtos.CategorySummaryDto;
import com.dev.dungcony.modules.product.dtos.req.CategoryAddReq;

public interface CategoryCommandService {
    CategorySummaryDto addNew(CategoryAddReq category);

    void delete(String code);
}
