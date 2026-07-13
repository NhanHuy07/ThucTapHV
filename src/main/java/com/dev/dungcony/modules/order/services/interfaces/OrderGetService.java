package com.dev.dungcony.modules.order.services.interfaces;

import com.dev.dungcony.modules.order.dtos.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes;
import com.dev.dungcony.modules.order.enums.OrderStatus;

import java.util.UUID;

public interface OrderGetService {

    OrderRes userGetOrderByCode(UUID userId, String orderCode);

    OrderRes adminGetOrderByCode(String orderCode);

    OrderDto getDtoByCode(String orderCode);

    Page<OrderSummaryRes> userGetOrders(UUID uId, Pageable pageable);

    Page<OrderSummaryRes> userGetOrdersByStatus(UUID uId, OrderStatus status, Pageable pageable);

    Page<OrderSummaryRes> adminGetAllOrders(Pageable pageable);

    Page<OrderSummaryRes> adminGetAllOrdersByStatus(OrderStatus status, Pageable pageable);
}
