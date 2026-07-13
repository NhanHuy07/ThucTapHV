package com.dev.dungcony.modules.product.controllers.admin;

import com.dev.dungcony.modules.product.dtos.req.ItemUpdateReq;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.dtos.req.IteamAddReq;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemCreateService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemGetService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/product")
@Tag(name = "Products")
public class AdminItemController {

    private final ItemCreateService itemCreateService;
    private final ItemUpdateService itemUpdateService;
    private final ItemGetService service;

    @Operation(summary = "thêm 1 item")
    @PostMapping("/add-items")
    public ResponseEntity<ApiRes<List<String>>> addItems(
            @RequestBody IteamAddReq req) {

        return ResponseEntity.status(201).body(ApiRes.success(
                "created",
                itemCreateService.createItems(req)));
    }

    @Operation(summary = "update số lượng item")
    @PutMapping("/item/update-quantity")
    public ResponseEntity<ApiRes<?>> updateItemQuantity(
            @RequestBody ItemUpdateReq req) {

        return ResponseEntity.ok().body(ApiRes.success(
                "updated",
                itemUpdateService.updateQuantity(req)));
    }

    @Operation(summary = "lấy toàn bộ size của sản phẩm")
    @GetMapping("/items")
    public ResponseEntity<ApiRes<?>> getItems(
            @RequestParam String code) {
        return ResponseEntity.ok()
                .body(ApiRes.success(
                        "list items",
                        service.getByProductCode(code)));
    }

}
