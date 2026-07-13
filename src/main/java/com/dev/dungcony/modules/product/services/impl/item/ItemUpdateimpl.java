package com.dev.dungcony.modules.product.services.impl.item;

import com.dev.dungcony.modules.product.dtos.req.ItemUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ItemRes;
import com.dev.dungcony.modules.product.entities.Item;
import com.dev.dungcony.modules.product.entities.ItemId;
import com.dev.dungcony.modules.product.enums.ItemStatus;
import com.dev.dungcony.modules.product.exceptions.ItemNotFoundException;
import com.dev.dungcony.modules.product.exceptions.ItemQuantityUnLimit;
import com.dev.dungcony.modules.product.mappers.ItemMapper;
import com.dev.dungcony.modules.product.repositories.ItemRepository;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemUpdateService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemUpdateimpl implements ItemUpdateService {

    private final ItemRepository itemRepository;
    private final SizeCacheService sizeCacheService;

    @Transactional
    @Override
    public ItemRes updateQuantity(ItemUpdateReq req) {
        Item item = get(req.productCode(), sizeCacheService.getIdBySize(req.size()));

        item.setQuantity(req.quantity());
        item.setStatus(req.status());

        itemRepository.save(item);

        return ItemMapper.toRes(item);
    }

    @Transactional
    @Override
    public void reduce(int productId, int sizeId, int quantity) {
        Item item = get(productId, sizeId);

        int newCnt = item.getQuantity() - quantity;

        if (newCnt < 0)
            throw new ItemQuantityUnLimit();

        item.setQuantity(newCnt);
        if (newCnt == 0)
            item.setStatus(ItemStatus.OUT_OF_STOCK);
    }

    @Transactional
    @Override
    public void increase(int productId, int sizeId, int quantity) {

        Item item = get(productId, sizeId);
        int newCnt = item.getQuantity() + quantity;

        item.setQuantity(newCnt);
        if (item.getStatus() == ItemStatus.OUT_OF_STOCK)
            item.setStatus(ItemStatus.AVAILABLE);

        itemRepository.save(item);

    }

    // ------------------------------ PRIVATE --------------------------------//

    private Item get(int pId, int sId) {
        return itemRepository.findByIdForUpdate(new ItemId(pId, sId))
                .orElseThrow(ItemNotFoundException::new);
    }
}
