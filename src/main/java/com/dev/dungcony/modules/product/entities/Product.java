package com.dev.dungcony.modules.product.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.product.enums.ProductStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tbl_products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 30)
    @NotNull
    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Size(max = 255)
    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @ColumnDefault("0")
    @Column(name = "quantity_sold")
    private int quantitySold;

    @ColumnDefault("0")
    @Column(name = "price")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "rated")
    private Float rated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Size(max = 255)
    @Column(name = "img")
    private String img; // thumbail

    @Size(max = 255)
    @Column(name = "video")
    private String video;
}