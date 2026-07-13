package com.dev.dungcony.modules.product.services.impl.product;

import com.dev.dungcony.modules.product.dtos.req.ProductUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.enums.ProductStatus;
import com.dev.dungcony.modules.product.exceptions.ProductConflictException;
import com.dev.dungcony.modules.product.exceptions.ProductNotFoundException;
import com.dev.dungcony.modules.product.mappers.ProductMapper;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j
@Service
public class ProductUpdateImpl implements ProductUpdateService {
    private final ProductRepository productRepository;

    @Override
    public void increaseSold(String code, int quantity) {

        Product product = productRepository.findByCode(code)
                .orElseThrow(ProductNotFoundException::new);

        int newCnt = product.getQuantitySold() + quantity;

        if (newCnt > 5000)
            product.setStatus(ProductStatus.BESTSELLER);

        product.setQuantitySold(newCnt);

        productRepository.save(product);
    }

    @Transactional
    @Override
    public ProductDetailRes update(String productCode, ProductUpdateReq req) {

        Product product = productRepository.findByCode(productCode)
                .orElseThrow(ProductNotFoundException::new);

        if (product.getStatus() == ProductStatus.DELETED)
            throw new ProductConflictException("product is deleted");


        // ===== BASIC FIELDS =====
        if (req.name() != null)
            product.setName(req.name());
        if (req.description() != null)
            product.setDescription(req.description());
        if (req.price() != null)
            product.setPrice(req.price());
        if (req.imgUrl() != null)
            product.setImg(req.imgUrl());
        if (req.rate() != null)
            product.setRated(req.rate());
        if (req.sold() != null)
            product.setQuantitySold(req.sold());
        if (req.videoUrl() != null)
            product.setVideo(req.videoUrl());

        return ProductMapper.toDetailRes(product);
    }
}
