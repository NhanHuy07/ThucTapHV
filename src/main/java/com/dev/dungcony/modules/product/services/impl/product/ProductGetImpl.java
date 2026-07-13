package com.dev.dungcony.modules.product.services.impl.product;

import com.dev.dungcony.commons.dtos.DiscountInfoDto;
import com.dev.dungcony.modules.product.dtos.ItemDto;
import com.dev.dungcony.modules.product.dtos.ProductDto;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.enums.CategoryStatus;
import com.dev.dungcony.modules.product.enums.ProductStatus;
import com.dev.dungcony.modules.product.exceptions.CategoryNotFoundException;
import com.dev.dungcony.modules.product.exceptions.ProductConflictException;
import com.dev.dungcony.modules.product.exceptions.ProductNotFoundException;
import com.dev.dungcony.modules.product.exceptions.ProductUnProcessException;
import com.dev.dungcony.modules.product.mappers.ProductMapper;
import com.dev.dungcony.modules.product.repositories.CategoryRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemGetService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCalculator;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCalculator.ProductPriceInput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductGetImpl implements ProductGetService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionCalculator promotionCalculator;

    private final ItemGetService itemGetService;

    @Override
    public Integer getIdByCode(String code) {
        return findByCode(code).getId();
    }

    @Override
    public ProductSummaryRes getById(int id) {

        Product product = findById(id);

        String categoryCode = product.getCategory() != null ? product.getCategory().getCode() : null;
        DiscountInfoDto discount = promotionCalculator.calculateFinalPrice(
                product.getCode(), categoryCode, product.getPrice());

        return ProductMapper.toSumaryRes(product).withDiscount(discount);
    }

    // dùng nội bộ với dto
    @Override
    @Transactional(readOnly = true)
    public ProductDto getDtoByCode(String code) {

        Product product = findByCode(code);

        // Tính discount cho sản phẩm
        String categoryCode = product.getCategory() != null ? product.getCategory().getCode() : null;
        DiscountInfoDto discount = promotionCalculator.calculateFinalPrice(
                product.getCode(), categoryCode, product.getPrice());

        // Lấy danh sách items (nếu có) để hiển thị chi tiết
        List<ItemDto> items = itemGetService.getByProductCode(product.getCode());

        return ProductMapper.toDto(product, items, discount);
    }

    @Override
    public Map<String, ProductDto> getDtoByCodes(List<String> codes) {
        List<Product> products = productRepository.findByCodeIn(codes);

        List<ProductPriceInput> productPriceInputs = new ArrayList<>();

        // Tính discount cho sản phẩm
        for (Product product : products) {
            String categoryCode = product.getCategory() != null ? product.getCategory().getCode() : null;
            productPriceInputs.add(new ProductPriceInput(product.getCode(), categoryCode, product.getPrice()));
        }
        Map<String, DiscountInfoDto> discount = promotionCalculator.calculateFinalPrices(productPriceInputs);
        Map<String, List<ItemDto>> items = itemGetService.getByproductCodes(codes);

        return ProductMapper.toMapDto(products, items, discount);
    }

    // trả về thông tin chi tiết
    @Override
    public ProductDetailRes getDetailByCode(String code) {

        Product product = findByCode(code);

        // Tính discount cho sản phẩm
        String categoryCode = product.getCategory() != null ? product.getCategory().getCode() : null;
        DiscountInfoDto discount = promotionCalculator.calculateFinalPrice(
                product.getCode(), categoryCode, product.getPrice());

        // Lấy danh sách items (nếu có) để hiển thị chi tiết
        List<ItemDto> items = itemGetService.getByProductCode(product.getCode());

        return ProductMapper.toDetailRes(product, items, discount);
    }

    // trả về toàn bộ
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryRes> getAll(Pageable pageable) {
        Page<ProductSummaryRes> page = productRepository.findProductList(
                ProductStatus.ACTIVE,
                pageable);

        return enrichWithDiscounts(page);
    }

    // tìm theo category
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryRes> getAllByCategoryCode(String categoryCode, Pageable pageable) {
        categoryRepository.findByCode(categoryCode)
                .filter(c -> c.getStatus() == CategoryStatus.ACTIVE)
                .orElseThrow(CategoryNotFoundException::new);

        Page<ProductSummaryRes> rawPage = productRepository.findAllByCategoryCode(categoryCode, pageable);

        return enrichWithDiscounts(rawPage);
    }

    // tìm theo keyword
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryRes> searchByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return getAll(pageable);
        }

        Page<ProductSummaryRes> page = productRepository.getAllByKeyword(
                ProductStatus.ACTIVE,
                keyword.trim(),
                pageable);

        return enrichWithDiscounts(page);
    }

    // lọc sản phẩm
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryRes> filter(String categoryCode, BigDecimal minPrice, BigDecimal maxPrice,
            String keyword, Pageable pageable) {

        if (categoryCode != null && !categoryCode.isBlank()) {
            categoryRepository.findByCode(categoryCode)
                    .filter(c -> c.getStatus() == CategoryStatus.ACTIVE)
                    .orElseThrow(CategoryNotFoundException::new);
        }

        String trimmedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String trimmedCategory = (categoryCode != null && !categoryCode.isBlank()) ? categoryCode.trim() : null;

        Page<ProductSummaryRes> page = productRepository.filterProducts(
                ProductStatus.ACTIVE,
                trimmedCategory,
                minPrice,
                maxPrice,
                trimmedKeyword,
                pageable);

        return enrichWithDiscounts(page);
    }

    // tra về các sản phẩm mua nhiều nhất
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryRes> getAllBestSeller(Pageable pageable) {
        Page<ProductSummaryRes> page = productRepository.findBestSellerProducts(
                ProductStatus.BESTSELLER,
                pageable);

        return enrichWithDiscounts(page);
    }

    // kiểm tra đủ sản phẩm không
    @Override
    public long countByCodes(List<String> codes) {

        long cnt = productRepository.countByCodes(codes);

        if (cnt < codes.size())
            throw new ProductConflictException("bạn nhập sai 1 thông tin vài sản phẩm");

        if (cnt > codes.size())
            throw new ProductUnProcessException();

        return cnt;
    }

    // ============================= PRIVATE HELPERS =========================

    // áp dụng promotion vào từng sản phẩm
    private Page<ProductSummaryRes> enrichWithDiscounts(Page<ProductSummaryRes> page) {
        List<ProductSummaryRes> content = page.getContent();
        if (content.isEmpty()) {
            return page;
        }

        // Tạo batch input
        List<ProductPriceInput> inputs = content.stream()
                .map(p -> new ProductPriceInput(
                        p.code(),
                        p.categoryCode() != null ? p.categoryCode() : null,
                        p.price()))
                .toList();

        // Batch calculation: 3 queries thay vì 3*N
        Map<String, DiscountInfoDto> discountMap = promotionCalculator.calculateFinalPrices(inputs);

        // Map discount vào từng product
        return page.map(p -> {
            DiscountInfoDto discount = discountMap.getOrDefault(
                    p.code(),
                    DiscountInfoDto.noDiscount(p.price()));
            return p.withDiscount(discount);
        });
    }

    // ------ tìm kiếm sản phẩm với code -----------/
    private Product findByCode(String code) {
        Product product = productRepository.findByCodeWithCategoryAndProvider(code)
                .orElseThrow(() -> new ProductNotFoundException("product not found"));

        // Không trả về product đã bị xóa
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ProductNotFoundException("product not found");
        }

        // Ẩn sản phẩm nếu category bị ẩn
        if (product.getCategory() != null && product.getCategory().getStatus() == CategoryStatus.HIDDEN) {
            throw new ProductNotFoundException("product not found");
        }

        return product;
    }

    // ------ tìm kiếm sản phẩm với id -----------/
    private Product findById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product not found"));

        // Không trả về product đã bị xóa
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ProductNotFoundException("product not found");
        }

        // Ẩn sản phẩm nếu category bị ẩn
        if (product.getCategory() != null && product.getCategory().getStatus() == CategoryStatus.HIDDEN) {
            throw new ProductNotFoundException("product not found");
        }

        return product;
    }
}
