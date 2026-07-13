package com.dev.dungcony.modules.product.entities;

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
public class ItemId implements Serializable {
    @Serial
    private static final long serialVersionUID = -2973611614658127979L;
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "size_id")
    private Integer sizeId;
}
