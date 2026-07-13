package com.dev.dungcony.modules.voucher.dto.res;

import com.dev.dungcony.modules.voucher.enums.DiscountType;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherType;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherRes(
        DiscountType discountType,
        VoucherType voucherType,
        VoucherStatus status,
        BigDecimal value,
        BigDecimal minOrderAmount,
        Instant endAt
) {
}
