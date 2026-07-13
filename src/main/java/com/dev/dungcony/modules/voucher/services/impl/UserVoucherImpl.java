package com.dev.dungcony.modules.voucher.services.impl;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.services.interfaces.users.UserGetService;
import com.dev.dungcony.modules.voucher.dto.res.UserVoucherRes;
import com.dev.dungcony.modules.voucher.entities.UserVoucher;
import com.dev.dungcony.modules.voucher.entities.UserVoucherId;
import com.dev.dungcony.modules.voucher.entities.Voucher;
import com.dev.dungcony.modules.voucher.enums.DiscountType;
import com.dev.dungcony.modules.voucher.enums.VoucherType;
import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.exceptions.UserVoucherNotAvailable;
import com.dev.dungcony.modules.voucher.mappers.UserVoucherMapper;
import com.dev.dungcony.modules.voucher.repositories.UserVoucherRepository;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherCreateService;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherGetService;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherUpdateService;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVoucherImpl implements UserVoucherCreateService, UserVoucherGetService, UserVoucherUpdateService {

    private final UserVoucherRepository userVoucherRepository;
    private final VoucherGetService voucherGetService;
    private final UserGetService userGetService;

    //---------------------------CREATE USER VOUCHER-----------------------------//

    @Override
    public void applyNewbieVoucher(UUID uid) {

        log.info("apply Newbie Voucher {}", uid);

        List<Voucher> vouchers = voucherGetService.getByTypeAndStatus(VoucherType.NEWBIE, VoucherStatus.ACTIVE);

        List<UserVoucher> uvs = new ArrayList<>();

        for (Voucher v : vouchers) {
            UserVoucher uv = new UserVoucher();
            uv.setId(new UserVoucherId(v.getId(), uid));
            uv.setStatus(UserVoucherStatus.AVAILABLE);
            uv.setVoucher(v);
            uvs.add(uv);
        }

        userVoucherRepository.saveAll(uvs);
    }

    //---------------------------GET USER VOUCHER-----------------------------//


    @Override
    public List<UserVoucherRes> getUserVouchers(UUID userId) {

        List<UserVoucher> uvs = userVoucherRepository.findAllByUserId(userId);

        return uvs.stream()
                .map(UserVoucherMapper::toRes)
                .toList();
    }

    @Override
    public List<UserVoucherRes> getUserVouchersByName(String name) {
        List<UserRes> users = userGetService.getByName(name);

        return users.stream()
                .flatMap(user -> userVoucherRepository.findAllByUserId(user.id()).stream())
                .map(UserVoucherMapper::toRes)
                .toList();
    }

    @Override
    public List<UserVoucherRes> getUserVouchersByStatus(UUID userId, UserVoucherStatus status) {
        List<UserVoucher> uvs = userVoucherRepository.findAllByUserIdByStatus(userId, status);

        return uvs.stream()
                .map(UserVoucherMapper::toRes)
                .toList();
    }

    @Override
    public BigDecimal applyVoucher(String code, UUID uuid, BigDecimal originalPrice) {
        UserVoucher userVoucher = getByCode(uuid, code);

        BigDecimal finalPrice = originalPrice;

        // Kiểm tra số tiền đơn hàng có đạt tối thiểu để áp dụng voucher không
        if (originalPrice.compareTo(userVoucher.getVoucher().getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("Số tiền đơn hàng không đủ để áp dụng voucher");
        }

        // Áp dụng giảm giá theo phần trăm hoặc số tiền cố định
        if (userVoucher.getVoucher().getDiscountType() == DiscountType.PERCENT) {
            // Giảm giá theo phần trăm
            BigDecimal discountRate = BigDecimal.valueOf(100).subtract(
                    userVoucher.getVoucher().getValue());

            finalPrice = finalPrice.multiply(discountRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Giảm giá số tiền cố định
            finalPrice = finalPrice.subtract(userVoucher.getVoucher().getValue());

            // Đảm bảo giá cuối cùng không âm
            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                finalPrice = BigDecimal.ZERO;
            }
        }

        return finalPrice;
    }

    //--------------------------- UPDATE USER VOUCHER -----------------------//

    @Transactional
    @Override
    public int checkOrUpdate(Instant now) {

        List<UserVoucherStatus> notChecks = List.of(UserVoucherStatus.EXPIRED, UserVoucherStatus.USED);

        return userVoucherRepository.checkOrUpdate(
                notChecks,
                now
        );
    }

    @Override
    public int updateStatus(UUID uid, String code, UserVoucherStatus status) {
        Voucher voucher = voucherGetService.getVoucherByCode(code);
        UserVoucherId id = new UserVoucherId(voucher.getId(), uid);

        UserVoucher uv = getById(id);

        uv.setStatus(status);

        userVoucherRepository.save(uv);

        return 1;
    }

    @Override
    public int apllyVoucherComplete(UUID uid, String code) {
        Voucher voucher = voucherGetService.getVoucherByCode(code);
        UserVoucherId id = new UserVoucherId(voucher.getId(), uid);

        UserVoucher uv = getById(id);

        uv.setStatus(UserVoucherStatus.USED);

        userVoucherRepository.save(uv);

        return 1;
    }


    //-------------------------------- PRIVATE -----------------------------//

    private UserVoucher getByCode(UUID uid, String code) {

        Voucher voucher = voucherGetService.getVoucherByCode(code);

        if (voucher.getVoucherType() == VoucherType.NEWBIE && !userGetService.isNewBie(uid))
            throw new UserVoucherNotAvailable();

        if (voucher.getVoucherType() == VoucherType.USER_RANK && !userGetService.rankIsValid(uid, voucher.getRankId()))
            throw new UserVoucherNotAvailable();


        UserVoucherId id = new UserVoucherId(voucher.getId(), uid);
        UserVoucher uv = userVoucherRepository.findById(id).orElse(null);

        if (uv == null) {
            uv = new UserVoucher();
            uv.setId(id);
            uv.setStatus(UserVoucherStatus.AVAILABLE);
            uv.setVoucher(voucher);
        } else {
            if (uv.getStatus() == UserVoucherStatus.EXPIRED || uv.getStatus() == UserVoucherStatus.USED)
                throw new UserVoucherNotAvailable("User has already used this voucher and it is expired");
        }

        userVoucherRepository.save(uv);

        return uv;
    }

    private UserVoucher getById(UserVoucherId id) {
        return userVoucherRepository.findById(id)
                .orElseThrow(UserVoucherNotAvailable::new);
    }

}
