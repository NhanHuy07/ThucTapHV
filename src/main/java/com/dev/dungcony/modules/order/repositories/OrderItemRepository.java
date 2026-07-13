package com.dev.dungcony.modules.order.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.order.dtos.OrderItemDto;
import com.dev.dungcony.modules.order.entities.OrderItem;
import com.dev.dungcony.modules.order.entities.OrderItemId;
import com.dev.dungcony.modules.order.enums.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

  @Query("""
      SELECT new com.dev.dungcony.modules.order.dtos.OrderItemDto(
          p.code, s.size, oi.quantity, oi.originalPrice, oi.finalPrice
      )
      FROM OrderItem oi, Product p, Size s
      WHERE oi.order.id = :orderId
        AND p.id = oi.id.productId
        AND s.id = oi.id.sizeId
      """)
  List<OrderItemDto> findAllByOrderId(@Param("orderId") Integer orderId);

  @Query("""
      SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
      FROM OrderItem oi
      JOIN oi.order o
      WHERE o.userId = :userId
        AND oi.id.productId = :productId
        AND o.status = :status
      """)
  boolean existsByUserIdAndProductIdAndOrderStatus(
      @Param("userId") UUID userId,
      @Param("productId") Integer productId,
      @Param("status") OrderStatus status);
}
