package com.dev.dungcony.modules.promotion.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class PromotionProductId implements Serializable {
    @Serial
    private static final long serialVersionUID = 4100589063436669117L;
    @Column(name = "product_id", columnDefinition = "int not null")
    private Integer productId;

    @Column(name = "promotion_id", columnDefinition = "int not null")
    private Integer promotionId;

}