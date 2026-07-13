package com.dev.dungcony.modules.order.mappers;

import com.dev.dungcony.modules.order.dtos.OrderDto;
import com.dev.dungcony.modules.order.dtos.OrderItemDto;
import com.dev.dungcony.modules.order.dtos.req.CreateOrderReq;
import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;

import java.util.List;
import java.util.UUID;

public class OrderMapper {

    public static OrderRes toOrderRes(Order order, List<OrderItemDto> items, ReceiverRes reciever) {
        return toOrderRes(order, items, reciever, null);
    }

    public static OrderRes toOrderRes(Order order, List<OrderItemDto> items, ReceiverRes reciever, String paymentUrl) {
        return new OrderRes(
                order.getCode(),
                order.getStatus(),
                order.getPaymentType(),
                order.getVoucherCode(),
                reciever,
                order.getNote(),
                items,
                order.getTotalPrice(),
                order.getVoucherDiscount(),
                order.getFinalPrice(),
                paymentUrl,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }

    public static OrderDto toDto(Order order, List<OrderItemDto> items, String paymentUrl) {
        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getCode(),
                order.getStatus(),
                order.getPaymentType(),
                items,
                order.getTotalPrice(),
                order.getVoucherDiscount(),
                order.getFinalPrice(),
                paymentUrl,
                order.getCreatedAt());
    }

    public static Order toEnity(UUID userId, CreateOrderReq req) {
        Order order = new Order();
        order.setCode(generateOrderCode());
        order.setUserId(userId);
        order.setReceiverId(req.recieverid());
        order.setPaymentType(req.paymentType());
        order.setVoucherCode(req.voucherCode());
        if (req.paymentType() == PaymentType.ONLINE) {
            order.setStatus(OrderStatus.UNPAID);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }
        order.setNote(req.note());

        return order;
    }

    private static String generateOrderCode() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp.substring(timestamp.length() - 8) + "-" + random;
    }
}
