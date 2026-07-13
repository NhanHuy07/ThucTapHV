package com.dev.dungcony.modules.promotion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import com.dev.dungcony.modules.promotion.enums.PromotionScope;
import com.dev.dungcony.modules.promotion.enums.PromotionStatus;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "tbl_promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int not null")
    private Integer id;

    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

    @ColumnDefault("CURRENT_TIMESTAMP(3)")
    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @ColumnDefault("1")
    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PromotionStatus status = PromotionStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private PromotionScope scope = PromotionScope.GLOBAL;

    @Version
    @NotNull
    @ColumnDefault("0")
    @Column(name = "version", nullable = false)
    private Long version;
}
