package com.dev.dungcony.modules.order.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_order_items")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;
    @NotNull
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @NotNull
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // ---fk---//
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;
}
