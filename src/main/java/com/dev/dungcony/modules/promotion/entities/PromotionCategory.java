package com.dev.dungcony.modules.promotion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "tbl_promotion_category")
public class PromotionCategory {
    @EmbeddedId
    private PromotionCategoryId id;

    @Column(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Integer categoryId;

    @NotNull
    @MapsId("promotionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
}