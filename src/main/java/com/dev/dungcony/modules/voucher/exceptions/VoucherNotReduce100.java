package com.dev.dungcony.modules.voucher.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class VoucherNotReduce100 extends UnProcessableException {
    public VoucherNotReduce100() {
        super("TOO LARGE", "Không thể giảm nhiều hơn 100%");
    }
}
