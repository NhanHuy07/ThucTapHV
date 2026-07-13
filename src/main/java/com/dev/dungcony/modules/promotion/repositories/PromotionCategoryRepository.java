package com.dev.dungcony.modules.promotion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.PromotionCategory;
import com.dev.dungcony.modules.promotion.entities.PromotionCategoryId;
import com.dev.dungcony.modules.promotion.enums.PromotionStatus;

import java.time.Instant;
import java.util.List;

public interface PromotionCategoryRepository extends JpaRepository<PromotionCategory, PromotionCategoryId> {

    @Query("""
            SELECT new com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes(
                        pp.promotion.id,
                        pp.promotion.value,
                        pp.promotion.startAt,
                        pp.promotion.endAt
                        )
            FROM PromotionCategory pp
            WHERE pp.categoryId = :categoryId
                 AND pp.promotion.status = :status
                 AND pp.promotion.endAt > :now
            ORDER BY pp.promotion.priority DESC
            """)
    List<PromotionSummaryRes> findByCategoryId(
            @Param("categoryId") Integer categoryId,
            @Param("now") Instant now,
            @Param("status") PromotionStatus status);

    /**
     * Batch query: lấy tất cả promotions đang active cho danh sách categoryIds.
     * Tránh N+1 khi cần tính giá cho nhiều sản phẩm thuộc nhiều category.
     */
    @Query("""
            SELECT pp.categoryId,
                   new com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes(
                       pp.promotion.id,
                       pp.promotion.value,
                       pp.promotion.startAt,
                       pp.promotion.endAt
                   )
            FROM PromotionCategory pp
            WHERE pp.categoryId IN :categoryIds
                 AND pp.promotion.status = :status
                 AND pp.promotion.endAt > :now
            ORDER BY pp.promotion.priority DESC
            """)
    List<Object[]> findByCategoryIds(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("now") Instant now,
            @Param("status") PromotionStatus status);

    /**
     * Kiểm tra category có promotion đang active hay không.
     * Sử dụng COUNT thay vì SELECT EXISTS để tương thích tốt hơn với JPQL.
     */
    @Query("""
            SELECT COUNT(pp) > 0
            FROM PromotionCategory pp
            WHERE pp.categoryId = :categoryId
                 AND pp.promotion.status = :status
                 AND pp.promotion.endAt > :now
            """)
    boolean existsActivePromotion(
            @Param("categoryId") Integer categoryId,
            @Param("now") Instant now,
            @Param("status") PromotionStatus status);

    void deleteAllByIdPromotionId(Integer promotionId);
}
