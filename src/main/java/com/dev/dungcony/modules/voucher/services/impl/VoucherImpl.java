package com.dev.dungcony.modules.voucher.services.impl;

import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankGetService;
import com.dev.dungcony.modules.voucher.dto.req.CreateVoucherReq;
import com.dev.dungcony.modules.voucher.dto.req.VoucherUpdateReq;
import com.dev.dungcony.modules.voucher.dto.res.VoucherRes;
import com.dev.dungcony.modules.voucher.entities.Voucher;
import com.dev.dungcony.modules.voucher.enums.DiscountType;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherType;
import com.dev.dungcony.modules.voucher.exceptions.VoucherCodeConflig;
import com.dev.dungcony.modules.voucher.exceptions.VoucherNotFoundException;
import com.dev.dungcony.modules.voucher.exceptions.VoucherNotReduce100;
import com.dev.dungcony.modules.voucher.exceptions.VoucherNotValid;
import com.dev.dungcony.modules.voucher.mappers.VoucherMapper;
import com.dev.dungcony.modules.voucher.repositories.UserVoucherRepository;
import com.dev.dungcony.modules.voucher.repositories.VoucherRepository;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherCreateService;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherGetService;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherImpl implements VoucherCreateService, VoucherGetService, VoucherUpdateService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final RankGetService rankGetService;

    //-------------------------------CREATE-------------------------------------------//
    @Transactional
    @Override
    public VoucherRes createVoucher(CreateVoucherReq req) {

        if (voucherRepository.existsByCode(req.code()))
            throw new VoucherCodeConflig();

        Voucher voucher = VoucherMapper.toEntity(req);

        validate(voucher, req.rankType());

        log.info("validate thanhf coong");
        voucher = voucherRepository.saveAndFlush(voucher);
        if (voucher.getId() == null) {
            throw new IllegalStateException("Voucher id was not generated");
        }

        int granted = grantVoucherToEligibleUsers(voucher);
        log.info("Granted {} user vouchers for voucher {}", granted, voucher.getCode());

        return VoucherMapper.toRes(voucher);
    }

    //-----------------------------GET VOUCHER-------------------------------------------//

    @Override
    public Voucher getVoucherByCode(String code) {

        Voucher v = voucherRepository.findByCode(code)
                .orElseThrow(VoucherNotFoundException::new);

        if (v.getStatus() != VoucherStatus.ACTIVE)
            throw new VoucherNotFoundException();

        return v;
    }

    @Override
    public List<Voucher> getByTypeAndStatus(VoucherType type, VoucherStatus status) {
        return voucherRepository.findAllByVoucherTypeAndStatus(type, status);
    }

    //----------------------------UPDATE VOUCHER-------------------------------------------//
    @Transactional
    @Override
    public int checkOrUpdate(Instant now) {
        return voucherRepository.checkOrUpdate(
                now,
                VoucherStatus.INACTIVE,
                VoucherStatus.COMMING_SOON,
                VoucherStatus.ACTIVE
        );
    }

    @Override
    public VoucherRes update(String vCode, VoucherUpdateReq req) {

        Voucher v = voucherRepository.findByCode(vCode)
                .orElseThrow(VoucherNotFoundException::new);

        if (req.voucherType() != null) v.setVoucherType(req.voucherType());

        // nếu voucher theo user rank
        if (req.value() != null) v.setValue(req.value());
        if (req.minOrderAmount() != null) v.setMinOrderAmount(req.minOrderAmount());
        if (req.startAt() != null) v.setStartAt(req.startAt());
        if (req.endAt() != null) v.setEndAt(req.endAt());

        validate(v, req.rankType());

        voucherRepository.save(v);

        return VoucherMapper.toRes(v);
    }


    //----------------------------PRIVATE VOUCHER-------------------------------------------//
    private void validate(Voucher voucher, RankType rankType) {
        Instant now = Instant.now();

        if (voucher.getStartAt() != null
                && voucher.getEndAt() != null
                && !voucher.getEndAt().isAfter(voucher.getStartAt()))
            throw new VoucherNotValid();

        if (voucher.getEndAt() != null && !voucher.getEndAt().isAfter(now)) {
            voucher.setStatus(VoucherStatus.INACTIVE);
        } else if (voucher.getStartAt() != null && voucher.getStartAt().isAfter(now)) {
            voucher.setStatus(VoucherStatus.COMMING_SOON);
        } else {
            voucher.setStatus(VoucherStatus.ACTIVE);
        }

        // nếu là percent thì giá trị < 100 và > 0
        if (voucher.getValue() == null || voucher.getValue().compareTo(BigDecimal.ZERO) <= 0)
            throw new VoucherNotValid();

        if (voucher.getDiscountType() == DiscountType.PERCENT
                && voucher.getValue().compareTo(BigDecimal.valueOf(100)) > 0)
            throw new VoucherNotReduce100();

        // nếu voucher theo user rank
        if (voucher.getVoucherType() == VoucherType.USER_RANK) {
            if (rankType != null) {
                Integer rankId = rankGetService.getRank(rankType).id();
                voucher.setRankId(rankId);
            }

            if (voucher.getRankId() == null)
                throw new VoucherNotValid();
        } else {
            voucher.setRankId(null);
        }
    }

    private int grantVoucherToEligibleUsers(Voucher voucher) {
        if (voucher.getStatus() == VoucherStatus.INACTIVE) {
            return 0;
        }

        return switch (voucher.getVoucherType()) {
            case GLOBAL -> userVoucherRepository.grantGlobalVoucher(
                    voucher.getId(),
                    voucher.getEndAt(),
                    voucher.getMinOrderAmount()
            );
            case NEWBIE -> userVoucherRepository.grantNewbieVoucher(
                    voucher.getId(),
                    voucher.getEndAt(),
                    voucher.getMinOrderAmount(),
                    Instant.now().minus(90, ChronoUnit.DAYS)
            );
            case USER_RANK -> userVoucherRepository.grantRankVoucher(
                    voucher.getId(),
                    voucher.getRankId(),
                    voucher.getEndAt(),
                    voucher.getMinOrderAmount()
            );
        };
    }
}
