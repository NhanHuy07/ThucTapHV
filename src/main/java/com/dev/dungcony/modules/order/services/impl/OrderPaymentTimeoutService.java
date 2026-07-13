package com.dev.dungcony.modules.order.services.impl;

import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.entities.OrderItem;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentTimeoutService {

    private final OrderRepository orderRepository;
    private final ItemUpdateService itemUpdateService;

    @Transactional
    public boolean cancelExpiredUnpaidOrder(Integer orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElse(null);

        if (order == null || order.getStatus() != OrderStatus.UNPAID) {
            return false;
        }

        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem orderItem : order.getItems()) {
            itemUpdateService.increase(
                    orderItem.getId().getProductId(),
                    orderItem.getId().getSizeId(),
                    orderItem.getQuantity());
        }

        log.info("Expired unpaid order auto-cancelled: {}", order.getCode());
        return true;
    }
}
