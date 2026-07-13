package com.dev.dungcony.modules.product.services.impl.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.product.dtos.res.ProviderRes;
import com.dev.dungcony.modules.product.enums.ProviderStatus;
import com.dev.dungcony.modules.product.exceptions.ProviderNotFoundException;
import com.dev.dungcony.modules.product.mappers.ProviderMapper;
import com.dev.dungcony.modules.product.repositories.ProviderRepository;
import com.dev.dungcony.modules.product.services.interfaces.provider.ProviderGetService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProviderGetServiceImpl implements ProviderGetService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Override
    public ProviderRes getByCode(String code) {
        return providerMapper.toRes(
                providerRepository.findByCode(code).orElseThrow(ProviderNotFoundException::new));
    }

    @Override
    public ProviderRes getByName(String name) {
        return providerMapper.toRes(
                providerRepository.findByName(name).orElseThrow(ProviderNotFoundException::new));
    }

    @Override
    public List<ProviderRes> getAll() {
        return providerRepository.findAll().stream().map(providerMapper::toRes).toList();
    }

    @Override
    public List<ProviderRes> getAllNew() {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime startOfDay = ZonedDateTime.now(zone).toLocalDate().atStartOfDay(zone);
        Instant start = startOfDay.toInstant();
        Instant end = startOfDay.plusDays(1).toInstant();
        return providerRepository.findAllCreatedBetween(start, end)
                .stream().map(providerMapper::toRes).toList();
    }

    @Override
    public List<ProviderRes> getAllFamous() {
        return providerRepository.findAllByStatus(ProviderStatus.FAMOUS)
                .stream().map(providerMapper::toRes).toList();
    }
}
