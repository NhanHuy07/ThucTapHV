package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class ProviderNotFoundException extends NotFoundException {
    public ProviderNotFoundException() {
        super("PROVIDER_NOT_FOUND", "Provider not found");
    }
}
