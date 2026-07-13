package com.dev.dungcony.modules.promotion.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dev.dungcony.modules.promotion.dtos.req.PromoAddReq;
import com.dev.dungcony.modules.promotion.dtos.req.PromoUpdateReq;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionDetailRes;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    PromotionDetailRes addNew(PromoAddReq req);

    PromotionDetailRes update(PromoUpdateReq req);

    void softDelete(Integer id);

    Page<PromotionSummaryRes> getAll(Pageable pageable);

    Optional<PromotionDetailRes> getById(Integer id);

    List<PromotionSummaryRes> getGlobalPromotions(Instant now);
}
