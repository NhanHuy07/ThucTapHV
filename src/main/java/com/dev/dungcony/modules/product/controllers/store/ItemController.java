package com.dev.dungcony.modules.product.controllers.store;

import com.dev.dungcony.modules.product.enums.ProductSize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemGetService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/public/product")
@Tag(name = "Products")
public class ItemController {

    private final ItemGetService service;

    @Operation(summary = "lấy thông tin item theo product code và size")
    @GetMapping("/item/{productCode}/size/{size}")
    public ResponseEntity<ApiRes<?>> getItems(
            @PathVariable String productCode,
            @PathVariable ProductSize size) {

        return ResponseEntity.ok().body(
                ApiRes.success(
                        "item",
                        service.getByProductCodeAndSize(productCode, size)));
    }

}
