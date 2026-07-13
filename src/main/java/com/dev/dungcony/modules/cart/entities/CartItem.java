package com.dev.dungcony.modules.cart.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.entities.Size;

import com.dev.dungcony.modules.users.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_cart_items")
public class CartItem extends BaseEntity {

    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sizeId")
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public CartItem(User user, Product product, Size size, int quantity) {
        this.id = new CartItemId(user.getId(), product.getId(), size.getId());
        this.user = user;
        this.product = product;
        this.size = size;
        this.quantity = quantity;
    }
}
