package com.dev.dungcony.modules.cart.repositories;

import com.dev.dungcony.modules.cart.entities.CartItem;
import com.dev.dungcony.modules.cart.entities.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<CartItem, CartItemId> {

        Optional<CartItem> findById_UserIdAndId_ProductIdAndId_SizeId(
                        UUID userId, Integer productId, Integer sizeId);

        void deleteAllById_UserId(UUID userId);

        // ------------------------------ QUERY ------------------------------------//
        @Query("SELECT COUNT(DISTINCT ci.product.code) FROM CartItem ci WHERE ci.id.userId = :userId AND ci.product.code IN :productCodes")
        long countDistinctProductCodesByUserIdAndCodes(UUID userId, List<String> productCodes);

        @Query("""
                        SELECT ci FROM CartItem ci
                        JOIN FETCH ci.product
                        JOIN FETCH ci.size
                        WHERE ci.user.id = :userId
                        ORDER BY ci.createdAt DESC
                        """)
        List<CartItem> findAllByUserId(@Param("userId") UUID userId);

}
