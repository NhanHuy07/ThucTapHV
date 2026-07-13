package com.dev.dungcony.modules.product.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.dtos.req.*;

import com.dev.dungcony.modules.product.services.interfaces.product.ProductAddService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductDeleteService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductUpdateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/product")
@Tag(name = "Products")
public class AdminProductController {

    private final ProductAddService productAddService;
    private final ProductUpdateService productUpdateService;
    private final ProductDeleteService productDeleteService;

    @PostMapping("/add-new")
    public ResponseEntity<ApiRes<?>> addNew(
            @RequestBody ProductAddReq req) {
        return ResponseEntity.ok()
                .body(ApiRes.success("Add new product successfully",
                        productAddService.addNew(req)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiRes<?>> update(
            @RequestParam("product_code") String productCode,
            @RequestBody ProductUpdateReq req) {
        return ResponseEntity.ok()
                .body(ApiRes.success("Update product successfully", productUpdateService.update(productCode, req)));
    }

    @DeleteMapping("/delete/{code}")
    public ResponseEntity<Void> delete(
            @PathVariable String code) {
        productDeleteService.delete(code);
        return ResponseEntity
                .ok()
                .build();
    }

}
