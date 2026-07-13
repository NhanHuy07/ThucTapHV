package com.dev.dungcony.modules.promotion.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCategoryId implements Serializable {
    @Serial
    private static final long serialVersionUID = -2973611614658127979L;
    @Column(name = "category_id", columnDefinition = "int not null")
    private Integer categoryId;

    @Column(name = "promotion_id", columnDefinition = "int not null")
    private Integer promotionId;

}