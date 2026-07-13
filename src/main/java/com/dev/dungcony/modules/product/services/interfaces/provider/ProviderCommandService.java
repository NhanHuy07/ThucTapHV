package com.dev.dungcony.modules.product.services.interfaces.provider;

import com.dev.dungcony.modules.product.dtos.req.ProviderAddReq;
import com.dev.dungcony.modules.product.dtos.req.ProviderUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ProviderRes;

public interface ProviderCommandService {
    ProviderRes addNew(ProviderAddReq dto);

    void delete(String code);

    ProviderRes update(String code, ProviderUpdateReq dto);
}
