package com.dev.dungcony.modules.product.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.dev.dungcony.modules.product.enums.ItemStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @EmbeddedId
    private ItemId id;

    @NotNull
    @MapsId("sizeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @NotNull
    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "quantity")
    private int quantity = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemStatus status;

    public Item(int productId, int sizeId, int quantity) {
        this.id = new ItemId(productId, sizeId);
        this.quantity = quantity;
        this.status = ItemStatus.AVAILABLE;
    }
}
