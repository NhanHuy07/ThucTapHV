package com.dev.dungcony.modules.voucher.services.interfaces;

import com.dev.dungcony.modules.voucher.dto.req.CreateVoucherReq;
import com.dev.dungcony.modules.voucher.dto.res.VoucherRes;

public interface VoucherCreateService {
    VoucherRes createVoucher(CreateVoucherReq req);

}