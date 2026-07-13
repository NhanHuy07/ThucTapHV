package com.dev.dungcony.modules.product.services.impl.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.req.ProviderAddReq;
import com.dev.dungcony.modules.product.dtos.req.ProviderUpdateReq;
import com.dev.dungcony.modules.product.dtos.res.ProviderRes;
import com.dev.dungcony.modules.product.entities.Provider;
import com.dev.dungcony.modules.product.enums.ProviderStatus;
import com.dev.dungcony.modules.product.exceptions.ProviderConflictException;
import com.dev.dungcony.modules.product.exceptions.ProviderNotFoundException;
import com.dev.dungcony.modules.product.mappers.ProviderMapper;
import com.dev.dungcony.modules.product.repositories.ProviderRepository;
import com.dev.dungcony.modules.product.services.interfaces.provider.ProviderCommandService;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProviderCommandServiceImpl implements ProviderCommandService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Transactional
    @Override
    public ProviderRes addNew(ProviderAddReq dto) {
        Provider provider = new Provider();
        provider.setName(dto.name());
        provider.setCode(dto.providerCode());
        provider.setEmail(dto.email());
        provider.setPhone(dto.phone());
        provider.setDescription(dto.description());
        provider.setLogo(dto.img());

        providerRepository.save(provider);

        return providerMapper.toRes(provider);
    }

    @Transactional
    @Override
    public void delete(String code) {
        Provider provider = providerRepository.findByCode(code)
                .orElseThrow(ProviderNotFoundException::new);

        if (provider.getStatus() == ProviderStatus.INACTIVE) {
            throw new ProviderConflictException("provider is already inactive");
        }

        provider.setStatus(ProviderStatus.INACTIVE);
    }

    @Transactional
    @Override
    public ProviderRes update(String code, ProviderUpdateReq dto) {
        Provider provider = providerRepository.findByCode(code)
                .orElseThrow(ProviderNotFoundException::new);

        if (provider.getStatus() == ProviderStatus.INACTIVE) {
            throw new ProviderConflictException("cannot update inactive provider");
        }

        if (dto.name() != null)
            provider.setName(dto.name());
        if (dto.email() != null)
            provider.setEmail(dto.email());
        if (dto.phone() != null)
            provider.setPhone(dto.phone());
        if (dto.description() != null)
            provider.setDescription(dto.description());
        if (dto.img() != null)
            provider.setLogo(dto.img());

        return providerMapper.toRes(provider);
    }
}
