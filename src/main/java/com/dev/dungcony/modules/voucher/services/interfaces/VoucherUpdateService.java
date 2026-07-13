package com.dev.dungcony.modules.voucher.services.interfaces;

import com.dev.dungcony.modules.voucher.dto.req.VoucherUpdateReq;
import com.dev.dungcony.modules.voucher.dto.res.VoucherRes;

import java.time.Instant;

public interface VoucherUpdateService {
    int checkOrUpdate(Instant now);

    VoucherRes update(String vCode, VoucherUpdateReq req);
}
