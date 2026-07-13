package com.dev.dungcony.modules.order.services.interfaces;

import com.dev.dungcony.modules.order.enums.OrderStatus;

import java.util.UUID;

public interface OrderUpdateService {
    // user hủy đơn hàng
    void userCancelOrder(UUID userId, String orderCode);

    //user thanh toán đơn hàng
    void userPaidOrder(UUID userId, String orderCode);

    //User xác nhận đã nhận được hàng => đơn hàng hoàn thành
    void userCompleteOrder(UUID userId, String orderCode);

    //admin xác nhận đơn hàng đang chờ
    void adminConfirmOrder(String orderCode);

    // admin cập nhật trạng thái đơn hàng thành đang giao hàng
    void adminShippingOrder(String orderCode);

    // admin xác nhận đơn hàng đã được giao
    void adminDeliveredOrder(String orderCode);

    //admin cập nhật trạng thái đơn hàng
    void adminUpdateOrderStatus(String orderCode, OrderStatus nextStatus);
}
