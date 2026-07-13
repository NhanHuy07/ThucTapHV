package com.dev.dungcony.modules.product.controllers.store;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.product.services.interfaces.provider.ProviderGetService;

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
@RequestMapping("/v1/api/public/provider")
@Tag(name = "Products")
public class ProviderController {
        private final ProviderGetService providerGetService;

        @GetMapping("/get-by-code/{code}")
        public ResponseEntity<ApiRes<?>> getByCode(
                        @PathVariable String code) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "provider",
                                                providerGetService.getByCode(code)));
        }

        @GetMapping("/get-by-name/{name}")
        public ResponseEntity<ApiRes<?>> getByName(
                        @PathVariable String name) {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "provider",
                                                providerGetService.getByName(name)));
        }

        @GetMapping("/gets")
        public ResponseEntity<ApiRes<?>> gets() {
                return ResponseEntity.ok()
                                .body(ApiRes.success(
                                                "provider",
                                                providerGetService.getAll()));
        }

}
