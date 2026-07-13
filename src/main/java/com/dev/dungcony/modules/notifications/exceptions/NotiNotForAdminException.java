package com.dev.dungcony.modules.notifications.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class NotiNotForAdminException extends UnauthorException {
    public NotiNotForAdminException() {
        super("NOT_FOR_ADMIN", "thông báo này không dành cho admin");
    }
}
