package com.dev.dungcony.modules.promotion.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class PromotionIsDelete extends ConflictException {
    public PromotionIsDelete() {
        super("Promotion is deleted");
    }
}
