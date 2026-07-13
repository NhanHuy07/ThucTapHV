package com.dev.dungcony.modules.product.services.impl.item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dev.dungcony.modules.product.dtos.res.ItemRes;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.entities.ItemId;
import com.dev.dungcony.modules.product.enums.ProductSize;
import com.dev.dungcony.modules.product.mappers.ItemMapper;
import com.dev.dungcony.modules.product.repositories.ItemRepository;
import com.dev.dungcony.modules.product.services.interfaces.GetIdByCode;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemGetService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemGetImpl implements ItemGetService {

    private final ItemRepository itemRepository;
    private final SizeCacheService sizeCacheService;
    private final GetIdByCode getIdByCode;

    @Override
    public List<ItemDto> getByProductId(Integer productId) {
        return itemRepository.findByIdProductId(productId).stream().map(ItemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> getByProductCode(String productCode) {
        Integer productId = getIdByCode.getByProductCode(productCode);
        return itemRepository.findByIdProductId(productId).stream().map(ItemMapper::toDto).toList();
    }

    @Override
    public Map<String, List<ItemDto>> getByproductCodes(List<String> productCodes) {
        return itemRepository.findByProductCodes(productCodes).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.groupingBy(ItemDto::productCode));
    }

    @Override
    public List<ItemDto> getBySizeId(Integer sizeId) {
        return itemRepository.findByIdSizeId(sizeId).stream().map(ItemMapper::toDto).toList();
    }

    @Override
    public List<ProductSize> getSizesByProductCode(String productCode) {
        List<Integer> sizeIds = itemRepository.findSizesByProductCode(productCode);

        return sizeIds.stream().map(sizeCacheService::getProductSizeById).toList();
    }

    @Override
    public ItemRes getByProductCodeAndSize(String productCode, ProductSize productSize) {
        Integer productId = getIdByCode.getByProductCode(productCode);
        Integer sizeId = sizeCacheService.getIdBySize(productSize);

        return itemRepository.findDetailByIdWithProductAndSize(new ItemId(productId, sizeId))
                .map(ItemMapper::toRes)
                .orElse(null);
    }
}
