package com.dev.dungcony.modules.product.controllers.store;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.product.dtos.res.ProductDetailRes;
import com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/public/product")
@Tag(name = "Products")
public class ProductController {
    private final ProductGetService productGetService;

    @Operation(summary = "Lấy danh sách sản phẩm", description = "Phân trang, hỗ trợ sort: ?page=0&size=10&sort=price,asc")
    @GetMapping("/get-all")
    public ResponseEntity<ApiRes<PageRes<ProductSummaryRes>>> getAll(
            @ParameterObject Pageable pageable) {

        Page<ProductSummaryRes> productPage = productGetService.getAll(pageable);

        return ResponseEntity.ok()
                .body(ApiRes.success(
                        "list product",
                        PageRes.from(productPage)));
    }

    @Operation(summary = "Lấy danh sách sản phẩm bán chạy")
    @GetMapping("/best-seller")
    public ResponseEntity<ApiRes<PageRes<ProductSummaryRes>>> getBestSeller(
            @ParameterObject Pageable pageable) {

        Page<ProductSummaryRes> productPage = productGetService.getAllBestSeller(pageable);

        return ResponseEntity.ok()
                .body(ApiRes.success(
                        "best seller products",
                        PageRes.from(productPage)));
    }

    @Operation(summary = "Xem chi tiết sản phẩm")
    @GetMapping("/get-by-code")
    public ResponseEntity<ApiRes<ProductDetailRes>> getById(
            @Parameter(description = "Mã sản phẩm") @RequestParam("code") String code) {
        return ResponseEntity.ok()
                .body(ApiRes.success("product", productGetService.getDetailByCode(code)));
    }

    @Operation(summary = "Tìm kiếm sản phẩm", description = "Tìm theo tên hoặc mô tả sản phẩm")
    @GetMapping("/search")
    public ResponseEntity<ApiRes<PageRes<ProductSummaryRes>>> search(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam("keyword") String keyword,
            @ParameterObject Pageable pageable) {
        Page<ProductSummaryRes> productPage = productGetService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok()
                .body(ApiRes.success("search results",
                        PageRes.from(productPage)));
    }

    @Operation(summary = "Lọc sản phẩm", description = "Lọc theo danh mục, khoảng giá, từ khóa. "
            + "Hỗ trợ sort: ?sort=price,asc | sort=price,desc | sort=createdAt,desc")
    @GetMapping("/filter")
    public ResponseEntity<ApiRes<PageRes<ProductSummaryRes>>> filter(
            @Parameter(description = "Mã danh mục (tùy chọn)") @RequestParam(value = "category_code", required = false) String categoryCode,
            @Parameter(description = "Giá tối thiểu") @RequestParam(value = "min_price", required = false) BigDecimal minPrice,
            @Parameter(description = "Giá tối đa") @RequestParam(value = "max_price", required = false) BigDecimal maxPrice,
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(value = "keyword", required = false) String keyword,
            @ParameterObject Pageable pageable) {

        Page<ProductSummaryRes> productPage = productGetService.filter(
                categoryCode, minPrice, maxPrice, keyword, pageable);

        return ResponseEntity.ok()
                .body(ApiRes.success("filtered products",
                        PageRes.from(productPage)));
    }

}
