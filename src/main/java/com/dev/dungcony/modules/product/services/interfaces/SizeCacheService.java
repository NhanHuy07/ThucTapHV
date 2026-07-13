package com.dev.dungcony.modules.product.services.interfaces;

import com.dev.dungcony.modules.product.enums.ProductSize;

public interface SizeCacheService {
    int getIdBySize(ProductSize size);

    ProductSize getProductSizeById(Integer id);
}
