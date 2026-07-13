package com.dev.dungcony.modules.product.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CommentId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "user_id")
    private UUID userId;
}
