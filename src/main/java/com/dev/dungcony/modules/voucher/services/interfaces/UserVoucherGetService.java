package com.dev.dungcony.modules.voucher.services.interfaces;

import com.dev.dungcony.modules.voucher.dto.res.UserVoucherRes;
import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserVoucherGetService {
    //trả về toàn bộ voucher của user
    List<UserVoucherRes> getUserVouchers(UUID userId);

    //trả về toàn bộ voucher của user
    List<UserVoucherRes> getUserVouchersByName(String name);

    //trả về toàn bộ của user với trạng thái cụ thể
    List<UserVoucherRes> getUserVouchersByStatus(UUID userId, UserVoucherStatus status);

    BigDecimal applyVoucher(String code, UUID uuid, BigDecimal originalPrice);
}
