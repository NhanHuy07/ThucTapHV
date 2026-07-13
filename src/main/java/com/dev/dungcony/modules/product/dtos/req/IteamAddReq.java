package com.dev.dungcony.modules.product.dtos.req;

import java.util.List;

import com.dev.dungcony.modules.product.dtos.ItemDto;

public record IteamAddReq(
        String productCode,
        List<ItemDto> items) {
}
