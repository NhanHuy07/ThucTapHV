package com.dev.dungcony.modules.promotion.mappers;


import com.dev.dungcony.modules.promotion.dtos.req.PromoAddReq;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionDetailRes;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;

public class PromotionMapper {

    /**
     * Entity → PromotionDetailRes (dùng cho endpoint get single promotion).
     * Không bao gồm internal id — client dùng code làm định danh.
     */
    public static PromotionDetailRes toDetailRes(Promotion p) {
        return new PromotionDetailRes(
                p.getId(),
                p.getValue(),
                p.getScope(),
                p.getStartAt(),
                p.getEndAt(),
                p.getPriority(),
                p.getStatus());
    }

    /**
     * Entity → PromotionSummaryRes (dùng cho danh sách / batch query).
     * Không bao gồm internal id — code là business key duy nhất.
     */
    public static PromotionSummaryRes toSummaryRes(Promotion p) {
        return new PromotionSummaryRes(
                p.getId(),
                p.getValue(),
                p.getStartAt(),
                p.getEndAt());
    }

    public static Promotion toEntity(PromoAddReq req) {
        Promotion p = new Promotion();
        p.setValue(req.value());
        p.setScope(req.scope());
        p.setStartAt(req.startAt());
        p.setEndAt(req.endAt());
        p.setPriority(req.priority());
        return p;
    }


}
