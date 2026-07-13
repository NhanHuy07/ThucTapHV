package com.dev.dungcony.modules.voucher.exceptions;

import com.dev.dungcony.commons.exceptions.InvalidException;


public class VoucherNotValid extends InvalidException {

    public VoucherNotValid() {
        super("400", "đầu vào không hợp lệ");
    }
}
