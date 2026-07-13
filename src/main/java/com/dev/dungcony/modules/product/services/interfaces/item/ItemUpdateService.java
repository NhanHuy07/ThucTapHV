package com.dev.dungcony.modules.product.services.interfaces.item;

import com.dev.dungcony.modules.product.dtos.req.ItemUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ItemRes;

public interface ItemUpdateService {
    ItemRes updateQuantity(ItemUpdateReq item);

    void reduce(int productId, int sizeId, int quantity);

    void increase(int productId, int sizeId, int quantity);
}
