package com.dev.dungcony.modules.product.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.product.entities.Size;
import com.dev.dungcony.modules.product.enums.ProductSize;
import com.dev.dungcony.modules.product.repositories.SizeRepository;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;

import jakarta.annotation.PostConstruct;

@Service
public class SizeCacheImpl implements SizeCacheService {

    private final SizeRepository sizeRepository;

    private Map<ProductSize, Size> sizeByName = new HashMap<>();
    private Map<Integer, Size> sizeById = new HashMap<>();

    public SizeCacheImpl(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @PostConstruct
    public void loadSizes() {
        List<Size> sizes = sizeRepository.findAll();

        sizeByName = sizes.stream()
                .collect(Collectors.toMap(Size::getSize, s -> s));

        sizeById = sizes.stream()
                .collect(Collectors.toMap(Size::getId, s -> s));

        System.out.println("Loaded " + sizes.size() + " sizes into cache");
    }

    @Override
    public int getIdBySize(ProductSize size) {
        return sizeByName.get(size).getId();
    }

    @Override
    public ProductSize getProductSizeById(Integer id) {
        return sizeById.get(id).getSize();
    }

}
