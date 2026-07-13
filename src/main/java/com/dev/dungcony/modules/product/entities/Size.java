package com.dev.dungcony.modules.product.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.product.enums.ProductSize;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_sizes")
public class Size extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private ProductSize size;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;
}
