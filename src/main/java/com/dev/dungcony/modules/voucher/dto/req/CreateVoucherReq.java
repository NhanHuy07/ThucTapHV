package com.dev.dungcony.modules.voucher.dto.req;

import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.voucher.enums.DiscountType;
import com.dev.dungcony.modules.voucher.enums.VoucherType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherReq(
        @NotBlank @NotNull @Size(max = 30) String code,
        @NotNull DiscountType discountType,
        @NotNull VoucherType voucherType,
        RankType rankType,
        @Min(1) BigDecimal value,
        @NotNull BigDecimal minOrderAmount,
        Instant startAt,
        Instant endAt) {
}
