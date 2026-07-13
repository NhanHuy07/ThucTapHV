package com.dev.dungcony.modules.product.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.dtos.req.CategoryAddReq;
import com.dev.dungcony.modules.product.services.interfaces.category.CategoryCommandService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/v1/api/admin/category")
@Tag(name = "Products")
public class AdminCategoryController {
        private final CategoryCommandService categoryCommandService;

        @PostMapping("/category/add-new")
        public ResponseEntity<ApiRes<?>> addNew(
                        @RequestBody CategoryAddReq req) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "Add new product successfully",
                                                categoryCommandService.addNew(req)));
        }

        @DeleteMapping("/category/{code}")
        public ResponseEntity<Void> delete(
                        @PathVariable String code) {
                categoryCommandService.delete(code);
                return ResponseEntity
                                .ok()
                                .build();
        }
}
