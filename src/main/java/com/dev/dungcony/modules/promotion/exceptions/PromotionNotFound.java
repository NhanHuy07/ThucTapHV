package com.dev.dungcony.modules.promotion.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class PromotionNotFound extends NotFoundException {
    public PromotionNotFound(String message) {
        super("PROMOTION_NOT_FOUND", message);
    }

    public PromotionNotFound() {
        super("PROMOTION_NOT_FOUND", "Promotion not found");
    }
}
