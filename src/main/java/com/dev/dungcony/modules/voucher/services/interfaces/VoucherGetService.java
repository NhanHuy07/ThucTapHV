package com.dev.dungcony.modules.voucher.services.interfaces;

import com.dev.dungcony.modules.voucher.entities.Voucher;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherType;

import java.util.List;

public interface VoucherGetService {
//    VoucherRes getByCode(String code);

    Voucher getVoucherByCode(String code);

    List<Voucher> getByTypeAndStatus(VoucherType type, VoucherStatus status);
}
