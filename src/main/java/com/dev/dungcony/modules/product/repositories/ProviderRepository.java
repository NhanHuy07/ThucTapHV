package com.dev.dungcony.modules.product.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.product.entities.Provider;
import com.dev.dungcony.modules.product.enums.ProviderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByCode(String code);

    Optional<Provider> findByName(String name);

    List<Provider> findAllByStatus(ProviderStatus status);

    @Query("SELECT p FROM Provider p WHERE p.createdAt >= :startOfDay AND p.createdAt < :endOfDay")
    List<Provider> findAllCreatedBetween(@Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay);
}
