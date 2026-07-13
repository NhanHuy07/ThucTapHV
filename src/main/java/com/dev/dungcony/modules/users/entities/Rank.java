package com.dev.dungcony.modules.users.entities;

import com.dev.dungcony.modules.users.enums.RankType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_ranks")
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", unique = true)
    @Enumerated(EnumType.STRING)
    private RankType type;

    @Column(name = "level", nullable = false, unique = true)
    private Integer level;

    @Column(name = "min_purchase")
    private BigDecimal minPurchase; // ngưỡng tiền dùng tối thiểu

    @Column(name = "expire_time")
    private Long expireTime; // thời gian hết hạn áp dụng mức rank

}
