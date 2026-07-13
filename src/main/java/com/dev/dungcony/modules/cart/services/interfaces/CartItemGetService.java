package com.dev.dungcony.modules.cart.services.interfaces;

import java.util.List;
import java.util.UUID;

public interface CartItemGetService {
    boolean existsLitProductCode(UUID uid, List<String> productCode);
}
