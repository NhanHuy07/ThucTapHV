package com.dev.dungcony.modules.promotion.services.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.dungcony.modules.promotion.enums.PromotionStatus;
import com.dev.dungcony.modules.promotion.repositories.PromotionRepository;

import java.time.Instant;

/**
 * Scheduled tasks để cập nhật trạng thái promotion.
 * Sử dụng bulk UPDATE query thay vì load entity rồi save lại,
 * giúp giảm số lượng query và memory usage.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl {

    private final PromotionRepository promotionRepository;

    /**
     * Chạy mỗi phút: đánh dấu ENDED cho các promotion ACTIVE đã hết hạn.
     * Sử dụng 1 bulk UPDATE query thay vì SELECT + loop + saveAll.
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void updateExpiredPromotions() {
        Instant now = Instant.now();

        int expiredCount = promotionRepository.bulkExpirePromotions(PromotionStatus.ACTIVE, now);

        if (expiredCount > 0) {
            log.info("Expired {} active promotions.", expiredCount);
        }
    }

    /**
     * Chạy mỗi phút: kích hoạt các promotion SCHEDULED đã đến giờ start.
     * Xử lý 2 trường hợp:
     * 1. Đã đến giờ start & chưa hết hạn -> ACTIVE
     * 2. Đã đến giờ start & đã hết hạn (KM rất ngắn) -> ENDED
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void activateScheduledPromotions() {
        Instant now = Instant.now();

        // Kích hoạt các promotion chưa hết hạn
        int activatedCount = promotionRepository.bulkActivatePromotions(PromotionStatus.SCHEDULED, now);

        // Đánh dấu ENDED cho các promotion SCHEDULED đã quá hạn luôn
        int expiredScheduledCount = promotionRepository.bulkExpireScheduledPromotions(PromotionStatus.SCHEDULED, now);

        if (activatedCount > 0) {
            log.info("Activated {} scheduled promotions.", activatedCount);
        }
        if (expiredScheduledCount > 0) {
            log.info("Expired {} overdue scheduled promotions.", expiredScheduledCount);
        }
    }
}
