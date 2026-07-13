package com.dev.dungcony.modules.promotion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_promotion_product")
public class PromotionProduct {
    @EmbeddedId
    private PromotionProductId id;

    @Column(name = "product_id", nullable = false, insertable = false, updatable = false)
    private Integer productId;

    @MapsId("promotionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
}