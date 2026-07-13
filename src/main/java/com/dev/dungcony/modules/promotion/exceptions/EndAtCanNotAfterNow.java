package com.dev.dungcony.modules.promotion.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class EndAtCanNotAfterNow extends UnProcessableException {
    public EndAtCanNotAfterNow() {
        super("INVALID_DATE", "End date must be after now");
    }
}
