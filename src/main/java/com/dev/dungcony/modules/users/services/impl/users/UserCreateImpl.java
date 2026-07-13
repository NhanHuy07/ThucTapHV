package com.dev.dungcony.modules.users.services.impl.users;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.entities.User;
import com.dev.dungcony.modules.users.mappers.UserMapper;
import com.dev.dungcony.modules.users.repositories.UserRepository;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankGetService;
import com.dev.dungcony.modules.users.services.interfaces.users.UserCreateService;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherCreateService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCreateImpl implements UserCreateService {
    private final UserRepository userRepository;
    private final UserVoucherCreateService userVoucherService;
    private final RankGetService rankGetService;

    @Transactional
    @Override
    public UserRes createUser(int accId) {
        User user = new User();

        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        user.setAccountId(accId);
        user.setRank(rankGetService.getByPurchase(BigDecimal.ZERO));
        log.info("bắt đầu khởi tạo user {}", user.getId());
        userRepository.saveAndFlush(user);
        log.info("User {} created", user.getId());
        userVoucherService.applyNewbieVoucher(uuid);

        return UserMapper.toUserRes(user);
    }

}
