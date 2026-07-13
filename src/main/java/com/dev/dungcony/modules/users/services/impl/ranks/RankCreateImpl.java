package com.dev.dungcony.modules.users.services.impl.ranks;

import com.dev.dungcony.modules.users.dto.req.RankAddReq;
import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.mappers.RankMapper;
import com.dev.dungcony.modules.users.repositories.RankRepository;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankCreateImpl implements RankCreateService {

    private final RankRepository rankRepository;


    @Override
    public String createRank(RankAddReq req) {

        Rank rank = RankMapper.toEntity(req);

        rankRepository.save(rank);
        return "/get/rank/" + rank.getId();
    }
}
