package com.dev.dungcony.modules.voucher.services.interfaces;

import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;

import java.time.Instant;
import java.util.UUID;

public interface UserVoucherUpdateService {
    int checkOrUpdate(Instant now);

    int updateStatus(UUID uid, String code, UserVoucherStatus status);

    int apllyVoucherComplete(UUID uid, String code);
}
