package com.dev.dungcony.modules.product.services.impl.product;

import com.dev.dungcony.modules.product.services.interfaces.product.ProductAddService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.req.ProductAddReq;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.entities.Category;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.entities.Provider;
import com.dev.dungcony.modules.product.enums.CategoryStatus;
import com.dev.dungcony.modules.product.enums.ProductStatus;
import com.dev.dungcony.modules.product.enums.ProviderStatus;
import com.dev.dungcony.modules.product.exceptions.CategoryNotFoundException;
import com.dev.dungcony.modules.product.exceptions.ProductConflictException;
import com.dev.dungcony.modules.product.exceptions.ProductNotFoundException;
import com.dev.dungcony.modules.product.exceptions.ProviderNotFoundException;
import com.dev.dungcony.modules.product.mappers.ProductMapper;
import com.dev.dungcony.modules.product.repositories.CategoryRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.repositories.ProviderRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductCommandImpl implements ProductAddService, ProductDeleteService {

    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public ProductDetailRes addNew(ProductAddReq req) {

        Category cate = categoryRepository.findByCode(req.categoryCode())
                .orElseThrow(CategoryNotFoundException::new);

        validateLeaf(cate);
        validateCategoryActive(cate);

        Provider provider = providerRepository.findByCode(req.providerCode())
                .orElseThrow(ProviderNotFoundException::new);

        validateProviderActive(provider);

        Product product = new Product();
        product.setCategory(cate);
        product.setProvider(provider);
        product.setName(req.name());
        product.setCode(generateProductCode(provider.getName(), req.name()));
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setImg(req.imgUrl());

        productRepository.save(product);

        return ProductMapper.toDetailRes(product);
    }

    @Transactional
    @Override
    public void delete(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(ProductNotFoundException::new);

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ProductConflictException("product is already deleted");
        }

        product.setStatus(ProductStatus.DELETED);
    }

    // check if category is leaf, only leaf category can contain product
    private void validateLeaf(Category cate) {
        if (!cate.getIsLeaf())
            throw new ProductConflictException("Category must be leaf");
    }

    private void validateCategoryActive(Category cate) {
        if (cate.getStatus() != CategoryStatus.ACTIVE)
            throw new ProductConflictException("Category is not active");
    }

    private void validateProviderActive(Provider provider) {
        if (provider.getStatus() == ProviderStatus.INACTIVE)
            throw new ProductConflictException("Provider is not active");
    }

    private String generateProductCode(String providerName, String name) {

        String provider = normalize(providerName).substring(0, Math.min(3, providerName.length()));
        String product = normalize(name).replace(" ", "");

        if (product.length() > 6) {
            product = product.substring(0, 6);
        }

        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return provider + "-" + product + "-" + random;
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replaceAll("[^a-zA-Z0-9 ]", "");
        return normalized.toUpperCase(Locale.ROOT);
    }
}
