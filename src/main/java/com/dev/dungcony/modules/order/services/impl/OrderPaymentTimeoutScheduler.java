package com.dev.dungcony.modules.order.services.impl;

import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.payment.config.PayOsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentTimeoutScheduler {

    private final PayOsProperties payOsProperties;
    private final OrderRepository orderRepository;
    private final OrderPaymentTimeoutService timeoutService;

    @Scheduled(fixedDelayString = "${order.payment-timeout-scan-delay-ms}")
    public void cancelExpiredUnpaidOnlineOrders() {
        Instant createdBefore = Instant.now()
                .minus(Duration.ofMinutes(payOsProperties.expiredMinutes()));

        List<Integer> expiredOrderIds = orderRepository.findExpiredPaymentOrderIds(
                OrderStatus.UNPAID,
                PaymentType.ONLINE,
                createdBefore);

        if (expiredOrderIds.isEmpty()) {
            return;
        }

        int cancelledCount = 0;
        for (Integer orderId : expiredOrderIds) {
            if (timeoutService.cancelExpiredUnpaidOrder(orderId)) {
                cancelledCount++;
            }
        }

        log.info("Expired unpaid order scan completed: found={}, cancelled={}",
                expiredOrderIds.size(), cancelledCount);
    }
}
