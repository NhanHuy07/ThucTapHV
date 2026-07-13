package com.dev.dungcony.modules.notifications.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class NotiNotFoundByCodeException extends NotFoundException {

    public NotiNotFoundByCodeException() {
        super("NOT_FOUND_BY_CODE", "không tìm thấy thông báo");
    }
}
