package com.dev.dungcony.modules.product.dtos.req;

import com.dev.dungcony.modules.product.enums.ItemStatus;
import com.dev.dungcony.modules.product.enums.ProductSize;

public record ItemUpdateReq(
                Integer productCode,
                ProductSize size,
                Integer quantity,
                ItemStatus status) {
}
