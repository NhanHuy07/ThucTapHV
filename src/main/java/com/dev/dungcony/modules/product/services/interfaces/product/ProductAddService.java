package com.dev.dungcony.modules.product.services.interfaces.product;

import com.dev.dungcony.modules.product.dtos.req.ProductAddReq;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;

public interface ProductAddService {

    ProductDetailRes addNew(ProductAddReq req);
}
