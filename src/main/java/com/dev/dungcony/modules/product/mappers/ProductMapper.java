package com.dev.dungcony.modules.product.mappers;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;
import com.dev.dungcony.modules.product.dtos.CategorySummaryDto;
import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.dtos.ProductDto;
import com.dev.dungcony.modules.product.dtos.ProviderSummaryDto;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes;
import com.dev.dungcony.modules.product.entities.Category;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.entities.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper chuyển đổi giữa Product entity và các DTO response.
 * Không bao gồm internal id — client dùng code làm định danh.
 */
public class ProductMapper {

    public static ProductSummaryRes toSumaryRes(Product p) {
        return new ProductSummaryRes(
                p.getCode(),
                p.getName(),
                p.getPrice(),
                p.getPrice(),
                p.getRated(),
                p.getImg(),
                p.getCategory().getCode());
    }

    /**
     * Entity → ProductDetailRes không có discount (cho create/update response).
     */
    public static ProductDetailRes toDetailRes(Product p) {
        return toDetailRes(p, null, null);
    }

    /**
     * Entity → ProductDetailRes với discount info, không có items.
     */
    public static ProductDetailRes toDetailRes(Product p, DiscountInfoDto discount) {
        return toDetailRes(p, null, discount);
    }

    /**
     * Entity → ProductDetailRes với discount info và items.
     */
    public static ProductDetailRes toDetailRes(Product p, List<ItemDto> items, DiscountInfoDto discount) {
        CategorySummaryDto catDto = null;
        Category c = p.getCategory();
        if (c != null) {
            catDto = new CategorySummaryDto(c.getName(), c.getCode());
        }

        ProviderSummaryDto provDto = null;
        Provider pv = p.getProvider();
        if (pv != null) {
            provDto = new ProviderSummaryDto(pv.getName(), pv.getCode());
        }

        return new ProductDetailRes(
                p.getName(),
                p.getCode(),
                p.getDescription(),
                p.getPrice(),
                discount != null ? discount.finalPrice() : p.getPrice(),
                discount != null ? discount.discountType() : "NONE",
                discount != null ? discount.discountValue() : 0,
                p.getQuantitySold(),
                p.getRated(),
                p.getImg(),
                p.getStatus(),
                items,
                p.getCreatedAt(),
                p.getUpdatedAt(),
                catDto,
                provDto);
    }

    public static ProductDto toDto(Product p, List<ItemDto> items, DiscountInfoDto discount) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getCode(),
                p.getDescription(),
                p.getPrice(),
                discount != null ? discount.finalPrice() : p.getPrice(),
                discount != null ? discount.discountType() : "NONE",
                discount != null ? discount.discountValue() : 0,
                p.getQuantitySold(),
                p.getRated(),
                p.getImg(),
                p.getStatus(),
                items,
                p.getCreatedAt(),
                p.getUpdatedAt());
    }

    public static Map<String, ProductDto> toMapDto(List<Product> p, Map<String, List<ItemDto>> items,
            Map<String, DiscountInfoDto> discounts) {

        Map<String, ProductDto> productDtos = new HashMap<>();

        for (Product p1 : p) {
            DiscountInfoDto discount = discounts.get(p1.getCode());
            List<ItemDto> itemDto = items.get(p1.getCode());

            productDtos.put(p1.getCode(), toDto(p1, itemDto, discount));
        }

        return productDtos;
    }

}
