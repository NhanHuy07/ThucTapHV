package com.dev.dungcony.modules.order.services.impl;

import com.dev.dungcony.modules.notifications.services.interfaces.NotificationCreateService;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.entities.OrderItem;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.exceptions.OrderConflictException;
import com.dev.dungcony.modules.order.exceptions.OrderUnAuthException;
import com.dev.dungcony.modules.order.exceptions.OrderNotFoundException;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.order.services.interfaces.OrderUpdateService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemUpdateService;
import com.dev.dungcony.modules.users.services.interfaces.users.UserUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderUpdateImpl implements OrderUpdateService {

    private final OrderRepository orderRepository;
    private final UserUpdateService userUpdateService;
    private final ItemUpdateService itemUpdateService;
    private final NotificationCreateService notificationCreateService;

    @Transactional
    @Override
    public void userCancelOrder(UUID userId, String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUserId().equals(userId))
            throw new OrderUnAuthException();

        OrderStatus oldStatus = order.getStatus();

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.UNPAID)
            throw new OrderConflictException("trạng thái hiện tại của đơn hàng không thể hủy");

        order.setStatus(OrderStatus.CANCELLED);

        // trả lại số lượng đơn hàng
        List<OrderItem> orderItems = order.getItems();
        for (OrderItem orderItem : orderItems) {
            itemUpdateService.increase(
                    orderItem.getId().getProductId(),
                    orderItem.getId().getSizeId(),
                    orderItem.getQuantity());
        }

        log.info("Order cancelled: {} by user: {}", orderCode, userId);

        if (oldStatus == OrderStatus.PENDING)
            userUpdateService.reducePurchaseAndUpdateRank(userId, order.getFinalPrice());

        notificationCreateService.userCancelOrder(userId);

    }

    @Transactional
    @Override
    public void userPaidOrder(UUID userId, String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUserId().equals(userId))
            throw new OrderUnAuthException();

        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new OrderConflictException("chỉ đơn hàng chưa thanh toán");
        }

        order.setStatus(OrderStatus.PENDING);
        notificationCreateService.userPailOrder(userId);

        userUpdateService.increasePurchaseAndUpdateRank(userId, order.getFinalPrice());

        log.info("thanh toán thành công");
    }

    @Transactional
    @Override
    public void userCompleteOrder(UUID userId, String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUserId().equals(userId))
            throw new OrderUnAuthException();

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new OrderConflictException("chỉ đơn hàng đã được giao");
        }

        order.setStatus(OrderStatus.COMPLETED);
        log.info("user {} đã nhận đơn hàng: {}", userId, orderCode);
    }

    @Transactional
    @Override
    public void adminConfirmOrder(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderConflictException("đơn hàng phải được thanh toán");
        }

        order.setStatus(OrderStatus.CONFIRMED);

        List<UUID> uid = List.of(order.getUserId());

        log.info("gửi thông báo cho user ..... {}", uid);
        notificationCreateService.adminComfirmOrders(uid);

        log.info("Order confirmed: {}", orderCode);
    }

    @Transactional
    @Override
    public void adminShippingOrder(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderConflictException("đơn hàng phải được xác nhận");
        }

        order.setStatus(OrderStatus.SHIPPING);
        log.info("Order shipping: {}", orderCode);
    }

    @Transactional
    @Override
    public void adminDeliveredOrder(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new OrderConflictException("đơn hàng phải được giao đến nơi");
        }

        List<UUID> uid = List.of(order.getUserId());

        notificationCreateService.adminDeliveredOrders(uid);

        order.setStatus(OrderStatus.DELIVERED);
        log.info("Order delivered: {}", orderCode);
    }

    @Override
    @Transactional
    public void adminUpdateOrderStatus(String orderCode, OrderStatus nextStatus) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(OrderNotFoundException::new);

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == nextStatus) {
            return;
        }

        validateStatusTransition(currentStatus, nextStatus);
        order.setStatus(nextStatus);
        if (nextStatus == OrderStatus.CANCELLED) {
            restoreProductQuantity(order);
        }
        notifyUserWhenAdminUpdatesStatus(order, nextStatus);
    }

    // ---PRIVATE---//
    private void restoreProductQuantity(Order order) {
        for (OrderItem orderItem : order.getItems()) {
            itemUpdateService.increase(
                    orderItem.getId().getProductId(),
                    orderItem.getId().getSizeId(),
                    orderItem.getQuantity());
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case UNPAID -> next == OrderStatus.PENDING;
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPING;
            case SHIPPING -> next == OrderStatus.DELIVERED || next == OrderStatus.RETURNED;
            case DELIVERED -> next == OrderStatus.RETURNED;
            case COMPLETED, CANCELLED, RETURNED -> false;
        };

        if (!valid) {
            throw new OrderConflictException(
                    "Cannot transition from " + current + " to " + next);
        }
    }

    private void notifyUserWhenAdminUpdatesStatus(Order order, OrderStatus nextStatus) {
        List<UUID> uid = List.of(order.getUserId());

        switch (nextStatus) {
            case CONFIRMED -> notificationCreateService.adminComfirmOrders(uid);
            case DELIVERED -> notificationCreateService.adminDeliveredOrders(uid);
            default -> {
            }
        }
    }

}
