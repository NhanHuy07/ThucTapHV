package com.dev.dungcony.modules.order.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.order.enums.OrderStatus;

import com.dev.dungcony.modules.order.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Size(max = 30)
    @Column(name = "voucher_code")
    private String voucherCode;

    @NotNull
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "voucher_discount")
    private BigDecimal voucherDiscount;

    @NotNull
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    //-----FK-----//
    @NotNull
    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @NotNull
    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

}
