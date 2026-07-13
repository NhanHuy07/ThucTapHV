package com.dev.dungcony.modules.product.services.impl.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.CategorySummaryDto;
import com.dev.dungcony.modules.product.dtos.req.CategoryAddReq;
import com.dev.dungcony.modules.product.entities.Category;
import com.dev.dungcony.modules.product.enums.CategoryStatus;
import com.dev.dungcony.modules.product.exceptions.CategoryCanNotCreateException;
import com.dev.dungcony.modules.product.exceptions.CategoryNotFoundException;
import com.dev.dungcony.modules.product.repositories.CategoryRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.category.CategoryCommandService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategorySummaryDto addNew(CategoryAddReq req) {

        Category parent = null;

        if (req.parentCode() != null && !req.parentCode().isBlank()) {

            parent = categoryRepository.findByCode(req.parentCode())
                    .orElseThrow(CategoryNotFoundException::new);

            if (parent.getStatus() != CategoryStatus.ACTIVE) {
                throw new CategoryCanNotCreateException(
                        "Cannot create sub-category under a hidden category");
            }

            if (productRepository.existsByCategory_Code(parent.getCode())) {
                throw new CategoryCanNotCreateException(
                        "Cannot create sub-category under a category that already contains products");
            }
        }

        Category category = new Category();
        category.setName(req.name());
        category.setCode(req.code());
        category.setDescription(req.description());
        category.setImgUrl(req.imgUrl());
        category.setParent(parent);
        category.setIsLeaf(true);

        if (parent == null) {
            category.setLevel(0);
            category.setPath("/" + req.code() + "/");
        } else {
            category.setLevel(parent.getLevel() + 1);
            category.setPath(parent.getPath() + req.code() + "/");
            parent.setIsLeaf(false);
        }

        categoryRepository.save(category);

        return new CategorySummaryDto(
                category.getName(),
                category.getCode());
    }

    @Override
    @Transactional
    public void delete(String code) {
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(CategoryNotFoundException::new);

        if (category.getStatus() == CategoryStatus.HIDDEN) {
            return; // already hidden
        }

        category.setStatus(CategoryStatus.HIDDEN);

        // Cascade HIDDEN to all sub-categories using path prefix
        if (category.getPath() != null) {
            categoryRepository.hideAllByPathPrefix(category.getPath());
        }
    }
}
