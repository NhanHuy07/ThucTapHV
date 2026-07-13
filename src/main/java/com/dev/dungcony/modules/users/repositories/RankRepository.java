package com.dev.dungcony.modules.users.repositories;

import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.enums.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Integer> {
    Optional<Rank> findByType(RankType type);

    Optional<Rank> findTopByMinPurchaseLessThanEqualOrderByMinPurchaseDesc(BigDecimal minPurchase);
}
