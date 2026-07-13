package com.dev.dungcony.modules.product.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
                SELECT new com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes(
                    p.code,
                    p.name,
                    p.price,
                    p.price,
                    p.rated,
                    p.img,
                    p.category.code
                )
                FROM Product p
                WHERE p.status = :status
            """)
    Page<ProductSummaryRes> findProductList(
            @Param("status") ProductStatus status,
            Pageable pageable);

    /**
     * Tìm sản phẩm theo keyword (tên hoặc mô tả).
     * Đã fix: trước đây không sử dụng tham số :key trong query.
     */
    @Query("""
                SELECT new com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes(
                    p.code,
                    p.name,
                    p.price,
                    p.price,
                    p.rated,
                    p.img,
                    p.category.code
                )
                FROM Product p
                WHERE p.status = :status
                    AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :key, '%'))
                         OR LOWER(p.description) LIKE LOWER(CONCAT('%', :key, '%')))
            """)
    Page<ProductSummaryRes> getAllByKeyword(
            @Param("status") ProductStatus status,
            @Param("key") String key,
            Pageable pageable);

    @Query("""
                SELECT new com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes(
                    p.code,
                    p.name,
                    p.price,
                    p.price,
                    p.rated,
                    p.img,
                    p.category.code
                )
                FROM Product p
                JOIN p.category c
                JOIN Category root ON root.code = :categoryCode
                WHERE p.status = com.dev.dungcony.modules.product.enums.ProductStatus.ACTIVE
                  AND c.path LIKE CONCAT(root.path, '%')
            """)
    Page<ProductSummaryRes> findAllByCategoryCode(
            @Param("categoryCode") String categoryCode,
            Pageable pageable);

    @Query("""
                SELECT new com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes(
                    p.code,
                    p.name,
                    p.price,
                    p.price,
                    p.rated,
                    p.img,
                    c.code
                )
                FROM Product p
                LEFT JOIN p.category c
                LEFT JOIN Category root ON root.code = :categoryCode
                WHERE p.status = :status
                  AND (:minPrice IS NULL OR p.price >= :minPrice)
                  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                  AND (:categoryCode IS NULL OR c.path LIKE CONCAT(root.path, '%'))
                  AND (:keyword IS NULL
                       OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS STRING), '%'))
                       OR LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS STRING), '%')))
            """)
    Page<ProductSummaryRes> filterProducts(
            @Param("status") ProductStatus status,
            @Param("categoryCode") String categoryCode,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
                SELECT new com.dev.dungcony.modules.product.dtos.res.ProductSummaryRes(
                    p.code,
                    p.name,
                    p.price,
                    p.price,
                    p.rated,
                    p.img,
                    p.category.code
                )
                FROM Product p
                WHERE p.status = :status
                ORDER BY p.quantitySold DESC, p.createdAt DESC
            """)
    Page<ProductSummaryRes> findBestSellerProducts(
            @Param("status") ProductStatus status,
            Pageable pageable);

    boolean existsByCategoryId(Integer id);

    boolean existsByCategory_Code(String code);

    /**
     * Kiểm tra tất cả product có tồn tại hay không.
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.code IN :codes")
    long countByCodes(
            @Param("codes") List<String> codes
    );

    Optional<Product> findByCode(String productCode);

    /**
     * Load product với category và provider theo mã sản phẩm (tránh N+1).
     */
    @Query("""
                SELECT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.provider
                WHERE p.code = :code
            """)
    Optional<Product> findByCodeWithCategoryAndProvider(@Param("code") String code);

    List<Product> findByCodeIn(List<String> codes);
}
