package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.InternalException;

public class SendEmailException extends InternalException {

    public SendEmailException() {
        super("500", "có lỗi khi gửi email");
    }
}
