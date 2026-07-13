package com.dev.dungcony.modules.order.repositories;

import java.util.Optional;
import java.util.UUID;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByCode(String orderCode);

    @Query("""
            SELECT o.id FROM Order o
            WHERE o.status = :status
              AND o.paymentType = :paymentType
              AND o.createdAt <= :createdBefore
            ORDER BY o.createdAt ASC
            """)
    List<Integer> findExpiredPaymentOrderIds(
            @Param("status") OrderStatus status,
            @Param("paymentType") PaymentType paymentType,
            @Param("createdBefore") Instant createdBefore);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT o FROM Order o
            WHERE o.id = :orderId
            """)
    Optional<Order> findByIdForUpdate(@Param("orderId") Integer orderId);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            WHERE o.code = :orderCode
            """)
    Optional<Order> findByOrderCodeWithItems(@Param("orderCode") String orderCode);

    @Query("""
            SELECT new com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes(
                o.code,
                o.status,
                o.finalPrice,
                SIZE(o.items),
                o.createdAt
            )
            FROM Order o
            WHERE o.userId = :userId
            ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryRes> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT new com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes(
                o.code,
                o.status,
                o.finalPrice,
                SIZE(o.items),
                o.createdAt
            )
            FROM Order o
            WHERE o.userId = :userId AND o.status = :status
            ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryRes> findAllByUserIdAndStatus(
            @Param("userId") UUID userId,
            @Param("status") OrderStatus status,
            Pageable pageable);

    @Query("""
            SELECT new com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes(
                o.code,
                o.status,
                o.finalPrice,
                SIZE(o.items),
                o.createdAt
            )
            FROM Order o
            ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryRes> findAllOrders(Pageable pageable);

    @Query("""
            SELECT new com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes(
                o.code,
                o.status,
                o.finalPrice,
                SIZE(o.items),
                o.createdAt
            )
            FROM Order o
            WHERE o.status = :status
            ORDER BY o.createdAt DESC
            """)
    Page<OrderSummaryRes> findAllByStatus(@Param("status") OrderStatus status, Pageable pageable);
}
