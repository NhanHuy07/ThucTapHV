package com.dev.dungcony.modules.users.services.impl.ranks;

import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.users.exceptions.RankNotFound;
import com.dev.dungcony.modules.users.mappers.RankMapper;
import com.dev.dungcony.modules.users.repositories.RankRepository;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RankGetImpl implements RankGetService {

    private final RankRepository rankRepository;

    @Override
    public RankRes getRank(RankType type) {

        Rank rank = rankRepository.findByType(type)
                .orElseThrow(RankNotFound::new);

        return RankMapper.toRes(rank);
    }

    @Override
    public Rank getByPurchase(BigDecimal purchase) {

        return rankRepository.findTopByMinPurchaseLessThanEqualOrderByMinPurchaseDesc(purchase)
                .orElse(null);
    }


}
