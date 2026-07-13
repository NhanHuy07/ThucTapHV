package com.dev.dungcony.modules.order.services.impl;

import java.util.List;
import java.util.UUID;

import com.dev.dungcony.modules.order.dtos.OrderDto;
import com.dev.dungcony.modules.order.dtos.OrderItemDto;
import com.dev.dungcony.modules.users.services.interfaces.receivers.RecieverGetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.dtos.res.OrderSummaryRes;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.exceptions.OrderNotFoundException;
import com.dev.dungcony.modules.order.mappers.OrderMapper;
import com.dev.dungcony.modules.order.repositories.OrderItemRepository;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.order.services.interfaces.OrderGetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderGetImpl implements OrderGetService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RecieverGetService recieverGetService;


    // ----------------------------------------------- USER ----------------------------------------//
    // find order of user
    @Override
    @Transactional(readOnly = true)
    public OrderRes userGetOrderByCode(UUID userId, String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException("Order not found");
        }

        List<OrderItemDto> items = orderItemRepository.findAllByOrderId(order.getId());

        return OrderMapper.toOrderRes(
                order,
                items,
                recieverGetService.getReceiverById(userId, order.getReceiverId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryRes> userGetOrders(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryRes> userGetOrdersByStatus(UUID userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    // ----------------------------------------------- ADMIN ----------------------------------------//
    @Override
    @Transactional(readOnly = true)
    public OrderRes adminGetOrderByCode(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        List<OrderItemDto> items = orderItemRepository.findAllByOrderId(order.getId());

        return OrderMapper.toOrderRes(
                order,
                items,
                recieverGetService.adminGetReceiverById(order.getReceiverId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryRes> adminGetAllOrders(Pageable pageable) {
        return orderRepository.findAllOrders(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryRes> adminGetAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findAllByStatus(status, pageable);
    }


    // ----------------------------------------------- DTO ----------------------------------------//

    @Override
    public OrderDto getDtoByCode(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        List<OrderItemDto> items = orderItemRepository.findAllByOrderId(order.getId());

        return OrderMapper.toDto(order, items, "");
    }

}
