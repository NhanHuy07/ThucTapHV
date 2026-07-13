package com.dev.dungcony.modules.voucher.exceptions;

import com.dev.dungcony.commons.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class VoucherNotFoundException extends AppException {
    public VoucherNotFoundException() {
        super(HttpStatus.NOT_FOUND, "VOUCHER_NOT_FOUND", "Voucher not found");
    }
}
