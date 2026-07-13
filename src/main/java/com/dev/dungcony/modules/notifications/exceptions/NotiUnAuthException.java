package com.dev.dungcony.modules.notifications.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class NotiUnAuthException extends UnauthorException {
    public NotiUnAuthException() {
        super("USER_NOT_AUTH", "không có quyền gửi thoong báo");
    }
}
