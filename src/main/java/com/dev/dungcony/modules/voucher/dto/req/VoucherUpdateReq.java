package com.dev.dungcony.modules.voucher.dto.req;

import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.voucher.enums.DiscountType;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherType;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherUpdateReq(
        VoucherType voucherType,
        RankType rankType,
        BigDecimal value,
        BigDecimal minOrderAmount,
        Instant startAt,
        Instant endAt
) {
}
