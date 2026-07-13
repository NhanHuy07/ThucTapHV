package com.dev.dungcony.modules.product.services.impl.category;

import com.dev.dungcony.modules.product.exceptions.CategoryConflict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.product.dtos.res.CategoryRes;
import com.dev.dungcony.modules.product.entities.Category;
import com.dev.dungcony.modules.product.enums.CategoryStatus;
import com.dev.dungcony.modules.product.exceptions.CategoryNotFoundException;
import com.dev.dungcony.modules.product.mappers.CategoryMapper;
import com.dev.dungcony.modules.product.repositories.CategoryRepository;
import com.dev.dungcony.modules.product.services.interfaces.category.CategoryGetService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryGetServiceImpl implements CategoryGetService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryRes> getAllChildren(String code) {
        List<Category> categories = categoryRepository.findAllChildrenByCode(code);

        return categories.stream().map(categoryMapper::toRes).toList();
    }

    @Override
    public List<CategoryRes> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toRes)
                .toList();
    }

    @Override
    public List<CategoryRes> getAllLeaf() {
        return categoryRepository.findAllLeafByStatus(CategoryStatus.ACTIVE)
                .stream()
                .map(categoryMapper::toRes)
                .toList();
    }

    @Override
    public CategoryRes getByCode(String code) {
        return categoryMapper.toRes(
                categoryRepository.findByCode(code).orElseThrow(CategoryNotFoundException::new));
    }

    @Override
    public CategoryRes getByName(String name) {
        return categoryMapper.toRes(
                categoryRepository.findByName(name).orElseThrow(CategoryNotFoundException::new));
    }

    @Override
    public long coutByCodes(List<String> codes) {
        long cnt = categoryRepository.cntByCodes(codes);

        if (cnt < codes.size())
            throw new CategoryConflict("1 số category bị sai");

        if (cnt > codes.size())
            throw new CategoryConflict("lỗi logic");

        return cnt;
    }
}
