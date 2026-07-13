package com.dev.dungcony.modules.payment.dtos.res;

public record PaymentRes(
        String orderCode,
        String paymentUrl
) {
}
