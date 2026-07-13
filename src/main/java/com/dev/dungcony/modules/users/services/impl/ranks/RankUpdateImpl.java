package com.dev.dungcony.modules.users.services.impl.ranks;

import com.dev.dungcony.modules.users.dto.req.RankUpdateReq;
import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.users.exceptions.RankNotFound;
import com.dev.dungcony.modules.users.mappers.RankMapper;
import com.dev.dungcony.modules.users.repositories.RankRepository;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankUpdateImpl implements RankUpdateService {

    private final RankRepository rankRepository;

    @Override
    public RankRes update(RankType type, RankUpdateReq req) {

        Rank rank = rankRepository.findByType(type)
                .orElseThrow(RankNotFound::new);

        rank.setMinPurchase(req.newMinPurchase());
        rank.setExpireTime(req.newExpireTime());

        rankRepository.save(rank);

        return RankMapper.toRes(rank);
    }
}
