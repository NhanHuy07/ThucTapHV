package com.dev.dungcony.modules.voucher.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class UserVoucherNotAvailable extends UnProcessableException {
    public UserVoucherNotAvailable() {
        super("422", "User cannot use this voucher");
    }

    public UserVoucherNotAvailable(String mes) {
        super("422", mes);
    }
}
