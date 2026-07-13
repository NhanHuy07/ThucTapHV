package com.dev.dungcony.modules.users.services.impl.users;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankGetService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.dto.req.UserUpdateReq;
import com.dev.dungcony.modules.users.entities.User;
import com.dev.dungcony.modules.users.exceptions.UserNotFound;
import com.dev.dungcony.modules.users.exceptions.UserUnAuthor;
import com.dev.dungcony.modules.users.mappers.UserMapper;
import com.dev.dungcony.modules.users.repositories.UserRepository;
import com.dev.dungcony.modules.users.services.interfaces.users.UserUpdateService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUpdateImpl implements UserUpdateService {

    private final UserRepository userRepository;
    private final RankGetService rankGetService;

    @Override
    @Transactional
    public UserRes updateUser(Integer accId, UserUpdateReq req) {

        UUID uuid = req.id();
        User user = userRepository.findById(uuid)
                .orElseThrow(UserNotFound::new);

        if (user.getAccountId() == null || !user.getAccountId().equals(accId))
            throw new UserUnAuthor();

        return getUserRes(req, user);
    }


    @Override
    @Transactional
    public UserRes adminUpdateUser(UserUpdateReq req) {
        UUID uuid = req.id();
        User user = userRepository.findById(uuid)
                .orElseThrow(UserNotFound::new);

        return getUserRes(req, user);
    }

    @Transactional
    @Override
    public void increasePurchaseAndUpdateRank(UUID userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        BigDecimal total = Optional.ofNullable(user.getToltalPurchase())
                .orElse(BigDecimal.ZERO)
                .add(amount);

        total = total.max(BigDecimal.ZERO);

        user.setToltalPurchase(total);

        Rank rank = rankGetService.getByPurchase(user.getToltalPurchase());

        user.setRank(rank);

        userRepository.save(user);

    }

    @Override
    public void reducePurchaseAndUpdateRank(UUID userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        BigDecimal total = Optional.ofNullable(user.getToltalPurchase())
                .orElse(BigDecimal.ZERO)
                .subtract(amount);

        user.setToltalPurchase(total);

        Rank rank = rankGetService.getByPurchase(user.getToltalPurchase());

        user.setRank(rank);

        userRepository.save(user);
    }


    @NonNull
    private UserRes getUserRes(UserUpdateReq req, User user) {
        if (req.firstName() != null)
            user.setFirstName(req.firstName());
        if (req.lastName() != null)
            user.setLastName(req.lastName());
        if (req.avatar() != null)
            user.setAvatar(req.avatar());

        userRepository.save(user);

        return UserMapper.toUserRes(user);
    }

}
