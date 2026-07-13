package com.dev.dungcony.modules.product.services.impl.item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.dtos.req.IteamAddReq;
import com.dev.dungcony.modules.product.entities.Item;
import com.dev.dungcony.modules.product.entities.ItemId;
import com.dev.dungcony.modules.product.repositories.ItemRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.repositories.SizeRepository;
import com.dev.dungcony.modules.product.services.interfaces.GetIdByCode;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemCreateService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemCreateImpl implements ItemCreateService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final GetIdByCode getIdByCode;
    private final SizeCacheService sizeCacheService;

    @Override
    public List<String> createItems(IteamAddReq items) {

        Integer productId = getIdByCode.getByProductCode(items.productCode());

        List<Item> tmp = new ArrayList<>();

        for (ItemDto item : items.items()) {
            Integer sizeId = sizeCacheService.getIdBySize(item.size());
            ItemId itemId = new ItemId(productId, sizeId);

            Item entity = itemRepository.findById(itemId).orElse(null);
            if (entity == null) {
                entity = new Item();
                entity.setId(itemId);
                entity.setProduct(productRepository.getReferenceById(productId));
                entity.setSize(sizeRepository.getReferenceById(sizeId));
                entity.setQuantity(item.quantity());
                entity.setStatus(item.status());
            } else {
                entity.setQuantity(item.quantity() + entity.getQuantity());
            }

            tmp.add(entity);
        }

        List<Item> savedItems = itemRepository.saveAll(tmp);

        return savedItems.stream()
                .map(item -> items.productCode() + "/size/" + item.getId())
                .collect(Collectors.toList());
    }
}
