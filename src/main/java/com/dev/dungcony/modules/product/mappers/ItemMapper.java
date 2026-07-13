package com.dev.dungcony.modules.product.mappers;

import com.dev.dungcony.modules.product.dtos.res.ItemRes;
import org.springframework.stereotype.Component;

import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.entities.Item;

@Component
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getProduct().getCode(),
                item.getSize().getSize(),
                item.getStatus(),
                item.getQuantity());
    }

    public static ItemRes toRes(Item item) {
        return new ItemRes(
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getProduct().getRated(),
                item.getProduct().getImg(),
                item.getSize().getSize());
    }
}
