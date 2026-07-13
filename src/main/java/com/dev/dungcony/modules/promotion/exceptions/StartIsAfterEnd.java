package com.dev.dungcony.modules.promotion.exceptions;

import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class StartIsAfterEnd extends UnProcessableException {

    public StartIsAfterEnd() {
        super("INVALID_DATE", "Start date must be after end date");
    }
}
