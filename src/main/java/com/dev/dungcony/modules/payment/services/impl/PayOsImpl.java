package com.dev.dungcony.modules.payment.services.impl;

import com.dev.dungcony.modules.order.dtos.OrderDto;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.enums.PaymentType;
import com.dev.dungcony.modules.order.exceptions.OrderConflictException;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.order.services.interfaces.OrderGetService;
import com.dev.dungcony.modules.order.services.interfaces.OrderUpdateService;
import com.dev.dungcony.modules.payment.config.PayOsProperties;
import com.dev.dungcony.modules.payment.dtos.res.PaymentRes;
import com.dev.dungcony.modules.payment.exceptions.PaymentException;
import com.dev.dungcony.modules.payment.exceptions.PaymentInvalidException;
import com.dev.dungcony.modules.payment.exceptions.PaymentUserIsIncorrectException;
import com.dev.dungcony.modules.payment.services.interfaces.PayOsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.core.ClientOptions;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayOsImpl implements PayOsService {

    private static final String SUCCESS_CODE = "00";
    private static final String CHECKOUT_BASE_URL = "https://pay.payos.vn/web/";

    private final PayOsProperties properties;
    private final OrderGetService orderGetService;
    private final OrderUpdateService orderUpdateService;
    private final OrderRepository orderRepository;

    private volatile PayOS payOS;

    @Override
    public PaymentRes createPaymentUrl(UUID userId, String orderCode, String ipAddress) {
        OrderDto order = orderGetService.getDtoByCode(orderCode);
        validateUserCanPay(userId, order);

        long amount = toPayOsAmount(order.finalPrice());
        long payOsOrderCode = order.id();

        CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                .orderCode(payOsOrderCode)
                .amount(amount)
                .description(buildDescription(payOsOrderCode))
                .cancelUrl(properties.resolvedCancelUrl())
                .returnUrl(properties.resolvedReturnUrl())
                .expiredAt(Instant.now().plusSeconds(properties.expiredMinutes() * 60).getEpochSecond())
                .build();

        try {
            CreatePaymentLinkResponse paymentLink = payOS().paymentRequests().create(paymentRequest);
            log.info("payOS payment link created for order: {} payosOrderCode={}", orderCode, payOsOrderCode);
            return toPaymentRes(order.orderCode(), paymentLink);
        } catch (PayOSException ex) {
            PaymentRes existingPayment = resolveExistingPayment(order, amount, ex);
            if (existingPayment != null) {
                return existingPayment;
            }
            throw new PaymentException("PAYOS_CREATE_LINK_FAILED", safeMessage(ex));
        }
    }

    @Override
    @Transactional
    public boolean processPaymentReturn(Map<String, String> params) {
        Long payOsOrderCode = parsePayOsOrderCode(params.get("orderCode"));
        if (payOsOrderCode == null) {
            return false;
        }

        try {
            PaymentLink paymentLink = payOS().paymentRequests().get(payOsOrderCode);
            return markPaidIfValid(
                    payOsOrderCode,
                    paymentLink.getAmount(),
                    paymentLink.getStatus(),
                    paymentLink.getId());
        } catch (PayOSException ex) {
            log.warn("payOS return verification failed: orderCode={}, error={}", payOsOrderCode, ex.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void processWebhook(Webhook webhook) {
        WebhookData data = verifyWebhook(webhook);
        if (data == null || data.getOrderCode() == null) {
            log.info("payOS webhook ignored: empty data");
            return;
        }

        if (!isSuccessfulWebhook(webhook, data)) {
            log.info("payOS webhook ignored: orderCode={}, code={}, dataCode={}",
                    data.getOrderCode(), webhook.getCode(), data.getCode());
            return;
        }

        markPaidIfValid(
                data.getOrderCode(),
                data.getAmount(),
                PaymentLinkStatus.PAID,
                data.getPaymentLinkId());
    }

    private PaymentRes resolveExistingPayment(OrderDto order, long amount, PayOSException createException) {
        try {
            PaymentLink paymentLink = payOS().paymentRequests().get((long) order.id());
            if (paymentLink.getAmount() != null && paymentLink.getAmount() != amount) {
                throw new PaymentException("PAYOS_AMOUNT_MISMATCH", "payOS amount does not match order amount");
            }
            if (paymentLink.getStatus() == PaymentLinkStatus.PAID) {
                markPaidIfValid(
                        paymentLink.getOrderCode(),
                        paymentLink.getAmount(),
                        paymentLink.getStatus(),
                        paymentLink.getId());
            }

            String paymentUrl = paymentLink.getId() == null ? null : CHECKOUT_BASE_URL + paymentLink.getId();
            log.info("payOS existing payment link reused for order: {}", order.orderCode());
            return new PaymentRes(order.orderCode(), paymentUrl);
        } catch (PaymentException ex) {
            throw ex;
        } catch (PayOSException ex) {
            log.warn("payOS create failed and existing link lookup failed: createError={}, lookupError={}",
                    createException.getMessage(), ex.getMessage());
            return null;
        }
    }

    private void validateUserCanPay(UUID userId, OrderDto order) {
        if (!order.uid().equals(userId)) {
            throw new PaymentUserIsIncorrectException();
        }
        if (order.paymentType() != PaymentType.ONLINE) {
            throw new PaymentInvalidException();
        }
        if (order.status() != OrderStatus.UNPAID) {
            throw new OrderConflictException("Order is not waiting for payment");
        }
    }

    private boolean markPaidIfValid(
            Long payOsOrderCode,
            Long paidAmount,
            PaymentLinkStatus payOsStatus,
            String paymentLinkId) {
        Optional<Order> orderOpt = findOrderByPayOsOrderCode(payOsOrderCode);
        if (orderOpt.isEmpty()) {
            log.info("payOS payment ignored: order not found for payosOrderCode={}", payOsOrderCode);
            return false;
        }

        Order order = orderOpt.get();
        if (payOsStatus != PaymentLinkStatus.PAID) {
            log.info("payOS payment not completed: order={}, status={}", order.getCode(), payOsStatus);
            return false;
        }

        validateOrderPayment(order, paidAmount);

        if (order.getStatus() == OrderStatus.UNPAID) {
            orderUpdateService.userPaidOrder(order.getUserId(), order.getCode());
            log.info("payOS payment success: order={}, paymentLinkId={}", order.getCode(), paymentLinkId);
        } else {
            log.info("payOS payment already processed: order={}, status={}", order.getCode(), order.getStatus());
        }

        return true;
    }

    private void validateOrderPayment(Order order, Long paidAmount) {
        if (order.getPaymentType() != PaymentType.ONLINE) {
            throw new PaymentException("PAYOS_NOT_ONLINE_ORDER", "Order is not an online payment order");
        }
        if (paidAmount == null) {
            throw new PaymentException("PAYOS_EMPTY_AMOUNT", "payOS payment amount is empty");
        }

        long expectedAmount = toPayOsAmount(order.getFinalPrice());
        if (expectedAmount != paidAmount) {
            throw new PaymentException("PAYOS_AMOUNT_MISMATCH", "payOS payment amount does not match order amount");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.RETURNED) {
            throw new PaymentException("PAYOS_ORDER_CLOSED", "Order is closed and cannot be paid");
        }
    }

    private Optional<Order> findOrderByPayOsOrderCode(Long payOsOrderCode) {
        if (payOsOrderCode == null || payOsOrderCode > Integer.MAX_VALUE || payOsOrderCode < Integer.MIN_VALUE) {
            return Optional.empty();
        }
        return orderRepository.findById(payOsOrderCode.intValue());
    }

    private WebhookData verifyWebhook(Webhook webhook) {
        try {
            return payOS().webhooks().verify(webhook);
        } catch (PayOSException ex) {
            throw new PaymentException("PAYOS_WEBHOOK_INVALID", safeMessage(ex));
        }
    }

    private boolean isSuccessfulWebhook(Webhook webhook, WebhookData data) {
        return Boolean.TRUE.equals(webhook.getSuccess())
                && SUCCESS_CODE.equals(webhook.getCode())
                && SUCCESS_CODE.equals(data.getCode());
    }

    private PaymentRes toPaymentRes(String orderCode, CreatePaymentLinkResponse paymentLink) {
        return new PaymentRes(orderCode, paymentLink.getCheckoutUrl());
    }

    private PayOS payOS() {
        if (!properties.configured()) {
            throw new PaymentException("PAYOS_NOT_CONFIGURED", "payOS is not configured");
        }
        PayOS current = payOS;
        if (current == null) {
            synchronized (this) {
                current = payOS;
                if (current == null) {
                    ClientOptions.ClientOptionsBuilder builder = ClientOptions.builder()
                            .clientId(properties.clientId())
                            .apiKey(properties.apiKey())
                            .checksumKey(properties.checksumKey());
                    if (properties.resolvedBaseUrl() != null) {
                        builder.baseURL(properties.resolvedBaseUrl());
                    }
                    current = new PayOS(builder.build());
                    payOS = current;
                }
            }
        }
        return current;
    }

    private long toPayOsAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new PaymentException("PAYOS_INVALID_AMOUNT", "Payment amount is invalid");
        }
        try {
            return amount.setScale(0, RoundingMode.UNNECESSARY).longValueExact();
        } catch (ArithmeticException ex) {
            throw new PaymentException("PAYOS_INVALID_AMOUNT", "payOS requires an integer VND amount");
        }
    }

    private Long parsePayOsOrderCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildDescription(long payOsOrderCode) {
        String value = "DH" + payOsOrderCode;
        if (value.length() <= 9) {
            return value;
        }
        return "DH" + Math.floorMod(payOsOrderCode, 10_000_000L);
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() == null ? "payOS payment error" : ex.getMessage();
    }
}
