package com.dev.dungcony.modules.product.services.interfaces.product;

import com.dev.dungcony.modules.product.dtos.req.ProductUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;

public interface ProductUpdateService {

    void increaseSold(String code, int quantity);

    ProductDetailRes update(String productCode, ProductUpdateReq req);
}
