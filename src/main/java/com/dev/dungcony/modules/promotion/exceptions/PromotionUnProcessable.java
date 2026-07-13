package com.dev.dungcony.modules.promotion.exceptions;


import com.dev.dungcony.commons.exceptions.UnProcessableException;

public class PromotionUnProcessable extends UnProcessableException {
    public PromotionUnProcessable(String message) {
        super("INVALID_PROMOTION", message);
    }
}
