package com.dev.dungcony.modules.product.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.dtos.req.ProviderAddReq;
import com.dev.dungcony.modules.product.dtos.req.ProviderUpdateReq;
import com.dev.dungcony.modules.product.services.interfaces.provider.ProviderCommandService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/provider")
@Tag(name = "Products")
public class AdminProviderController {

        private final ProviderCommandService providerCommandService;

        @PostMapping("add-new")
        public ResponseEntity<ApiRes<?>> addProvider(
                        @RequestBody ProviderAddReq req) {
                return ResponseEntity.ok()
                                .body(ApiRes.success("Add new product successfully",
                                                providerCommandService.addNew(req)));
        }

        @PutMapping("update/{code}")
        public ResponseEntity<ApiRes<?>> update(
                        @PathVariable String code,
                        @RequestBody ProviderUpdateReq req) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "Update product successfully",
                                                providerCommandService.update(code, req)));
        }

        @DeleteMapping("delete/{code}")
        public ResponseEntity<Void> deleteProvider(
                        @PathVariable String code) {
                providerCommandService.delete(code);
                return ResponseEntity
                                .ok()
                                .build();
        }

}
