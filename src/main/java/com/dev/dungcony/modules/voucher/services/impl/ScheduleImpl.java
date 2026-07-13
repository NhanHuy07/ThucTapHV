package com.dev.dungcony.modules.voucher.services.impl;

import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherUpdateService;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherUpdateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleImpl {

    private final VoucherUpdateService voucherUpdateService;
    private final UserVoucherUpdateService userVoucherUpdateService;

    //tự động chạy vào mỗi phút để kiểm tra và cập nhật trạng thái của voucher và user voucher đã hết hạn
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void checkUpdateVoucher() {
        Instant now = Instant.now();
        log.info("Deactivated {} expired vouchers.", voucherUpdateService.checkOrUpdate(now));
        log.info("Deactivated {} expired vouchers.", userVoucherUpdateService.checkOrUpdate(now));
    }
}
