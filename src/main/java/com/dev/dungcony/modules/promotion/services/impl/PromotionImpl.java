package com.dev.dungcony.modules.promotion.services.impl;

import com.dev.dungcony.modules.promotion.dtos.req.PromoAddReq;
import com.dev.dungcony.modules.promotion.dtos.req.PromoUpdateReq;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionDetailRes;
import com.dev.dungcony.modules.promotion.dtos.res.PromotionSummaryRes;
import com.dev.dungcony.modules.promotion.entities.Promotion;
import com.dev.dungcony.modules.promotion.enums.PromotionScope;
import com.dev.dungcony.modules.promotion.enums.PromotionStatus;
import com.dev.dungcony.modules.promotion.exceptions.*;
import com.dev.dungcony.modules.promotion.mappers.PromotionMapper;
import com.dev.dungcony.modules.promotion.repositories.PromotionRepository;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionCategoryService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionProductService;
import com.dev.dungcony.modules.promotion.services.interfaces.PromotionService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service quản lý CRUD promotion.
 * Logic tính toán giá đã được tách sang {@link PromotionCalculatorImpl}.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductService promotionProductService;
    private final PromotionCategoryService promotionCategoryService;

    //thêm 1 sản phẩm
    @Transactional
    @Override
    public PromotionDetailRes addNew(PromoAddReq req) {
        log.info("Adding new promotion: {}", req);

        Promotion promotion = PromotionMapper.toEntity(req);
        promotion.setStatus(initSatus(req.startAt(), req.endAt()));

        // Tạo mapping với product/category
        if (req.scope() == PromotionScope.PRODUCT) {
            promotionProductService.addListPromotionProduct(promotion, req.productCodes());
        }
        if (req.scope() == PromotionScope.CATEGORY) {
            promotionCategoryService.addListPromotionCategory(promotion, req.categoryCodes());
        }

        promotion = promotionRepository.save(promotion);

        return PromotionMapper.toDetailRes(promotion);

    }

    @Override
    public void softDelete(Integer id) {
        log.info("Soft-deleting promotion id={}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFound("Promotion not found with code: " + id));

        promotion.setStatus(PromotionStatus.DELETED);
        promotionRepository.save(promotion);
    }

    @Override
    public Page<PromotionSummaryRes> getAll(Pageable pageable) {
        return promotionRepository.getAll(pageable);
    }

    @Transactional
    @Override
    public PromotionDetailRes update(PromoUpdateReq req) {
        log.info("Updating promotion code={}", req.id());

        Promotion promotion = promotionRepository.findById(req.id())
                .orElseThrow(PromotionNotFound::new);

        // Không cho update promotion đã DELETED
        if (promotion.getStatus() == PromotionStatus.DELETED)
            throw new PromotionIsDelete();

        // Xác định startAt và endAt cuối cùng để validate
        Instant newStart = req.startAt() != null ? req.startAt() : promotion.getStartAt();
        Instant newEnd = req.endAt() != null ? req.endAt() : promotion.getEndAt();

        // tgian kết thúc < tgian bắt đầu
        if (newEnd.isBefore(newStart))
            throw new StartIsAfterEnd();

        Instant now = Instant.now();

        if (newEnd.isBefore(now)) {
            promotion.setStatus(PromotionStatus.ENDED);
        } else if (newStart.isAfter(now)) {
            promotion.setStatus(PromotionStatus.SCHEDULED);
        } else {
            promotion.setStatus(PromotionStatus.ACTIVE);
        }

        promotion.setStartAt(newStart);
        promotion.setEndAt(newEnd);

        // Update only provided fields
        if (req.value() != null)
            promotion.setValue(req.value());
        if (req.priority() != null)
            promotion.setPriority(req.priority());

        promotionRepository.save(promotion);
        log.info("Successfully updated promotion code={}", req.id());

        return PromotionMapper.toDetailRes(promotion);
    }

    // ----------------------------------- GET -----------------------------------//

    @Override
    public Optional<PromotionDetailRes> getById(Integer id) {
        return promotionRepository.findById(id)
                .map(p -> new PromotionDetailRes(
                        p.getId(),
                        p.getValue(),
                        p.getScope(),
                        p.getStartAt(),
                        p.getEndAt(),
                        p.getPriority(),
                        p.getStatus()));
    }

    @Override
    public List<PromotionSummaryRes> getGlobalPromotions(Instant now) {
        return promotionRepository.findGlobalPromotions(now, PromotionStatus.ACTIVE);
    }
//    // ============ PRIVATE HELPERS ============

    /**
     * Xác định status ban đầu: nếu startAt đã qua -> ACTIVE, nếu chưa -> SCHEDULED.
     */
    private PromotionStatus initSatus(Instant startAt, Instant endAt) {
        Instant now = Instant.now();
        if (endAt.isBefore(now)) {
            throw new PromotionUnProcessable("Cannot create promotion that has already ended");
        }
        return startAt.isBefore(now) ? PromotionStatus.ACTIVE : PromotionStatus.SCHEDULED;
    }

}
