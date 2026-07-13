package com.dev.dungcony.modules.product.services.interfaces.provider;

import java.util.List;

import com.dev.dungcony.modules.product.dtos.res.ProviderRes;

public interface ProviderGetService {

    ProviderRes getByCode(String code);

    ProviderRes getByName(String name);

    List<ProviderRes> getAll();

    List<ProviderRes> getAllNew();

    List<ProviderRes> getAllFamous();
}
