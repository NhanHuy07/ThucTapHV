package com.dev.dungcony.modules.product.entities;

import com.dev.dungcony.commons.entities.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_comments")
public class Comment extends BaseEntity {

    @EmbeddedId
    private CommentId id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "rating", nullable = true)
    private Float rating;

    @MapsId("productId")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
