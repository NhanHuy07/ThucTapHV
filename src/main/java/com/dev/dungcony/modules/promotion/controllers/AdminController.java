package com.dev.dungcony.modules.promotion.controllers;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.promotion.dtos.req.PromoAddReq;
import com.dev.dungcony.modules.promotion.dtos.req.PromoUpdateReq;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Promotions")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/admin/promotions")
public class AdminController {

    private final PromotionService promotionService;

    @GetMapping("/get-all")
    public ResponseEntity<ApiRes<?>> getAll(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiRes.success("list promotion", promotionService.getAll(pageable)));
    }

    @PostMapping("/add-new")
    public ResponseEntity<ApiRes<?>> addNew(
            @Valid @RequestBody PromoAddReq req) {


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiRes.success("add new promotion", promotionService.addNew(req)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiRes<?>> update(
            @Valid @RequestBody PromoUpdateReq req) {
        return ResponseEntity.ok()
                .body(ApiRes.success("Promotion updated successfully", promotionService.update(req)));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<ApiRes<Void>> deleteById(
            @PathVariable Integer id) {
        promotionService.softDelete(id);

        return ResponseEntity.ok().body(
                ApiRes.success("xóa thành công")
        );
    }
}
