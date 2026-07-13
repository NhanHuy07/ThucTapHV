package com.dev.dungcony.modules.voucher.mappers;

import com.dev.dungcony.modules.voucher.dto.res.UserVoucherRes;
import com.dev.dungcony.modules.voucher.entities.UserVoucher;

public class UserVoucherMapper {
    public static UserVoucherRes toRes(UserVoucher uv) {
        return new UserVoucherRes(
                uv.getVoucher().getCode(),
                uv.getVoucher().getDiscountType(),
                uv.getVoucher().getValue(),
                uv.getVoucher().getMinOrderAmount(),
                uv.getStatus(),
                uv.getEndAt()
        );
    }
}
