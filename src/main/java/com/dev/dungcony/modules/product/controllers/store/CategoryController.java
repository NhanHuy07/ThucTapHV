package com.dev.dungcony.modules.product.controllers.store;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.services.interfaces.category.CategoryGetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/public/category")
@Tag(name = "Products")
public class CategoryController {

        private final CategoryGetService service;

        @Operation(summary = "lấy toàn bộ danh mục", description = "Phân trang, hỗ trợ sort: ?page=0&size=10&sort=price,asc")
        @GetMapping("/get-all")
        public ResponseEntity<ApiRes<?>> getAll() {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "list category",
                                                service.getAll()));
        }

        @Operation(summary = "get all leaf categories", description = "Return active leaf categories for product creation")
        @GetMapping("/get-all-leaf")
        public ResponseEntity<ApiRes<?>> getAllLeaf() {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "list leaf category",
                                                service.getAllLeaf()));
        }

        @Operation(summary = "lấy danh sách danh mục con", description = "Phân trang, hỗ trợ sort: ?page=0&size=10&sort=price,asc")
        @GetMapping("/get-children/{code}")
        public ResponseEntity<ApiRes<?>> getAllChildren(
                        @PathVariable String code) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "list category",
                                                service.getAllChildren(code)));
        }

        @Operation(summary = "lấy thông tin danh mục", description = "Phân trang, hỗ trợ sort: ?page=0&size=10&sort=price,asc")
        @GetMapping("/get/{code}")
        public ResponseEntity<ApiRes<?>> getByCode(
                        @PathVariable String code) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "category",
                                                service.getByCode(code)));
        }

}
