package com.dev.dungcony.modules.voucher.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class VoucherCodeConflig extends ConflictException {

    public VoucherCodeConflig() {
        super("code is already exists");
    }
}
