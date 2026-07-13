package com.dev.dungcony.modules.product.services.interfaces.item;

import java.util.List;

import com.dev.dungcony.modules.product.dtos.req.IteamAddReq;

public interface ItemCreateService {
    List<String> createItems(IteamAddReq items);
}
