package com.dev.dungcony.modules.users.services.impl.users;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.entities.User;
import com.dev.dungcony.modules.users.exceptions.UserNotFound;
import com.dev.dungcony.modules.users.mappers.UserMapper;
import com.dev.dungcony.modules.users.repositories.UserRepository;
import com.dev.dungcony.modules.users.services.interfaces.users.UserGetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserGetImpl implements UserGetService {

    private final UserRepository userRepository;

    @Override
    public UserRes getUserByAccId(int accId) {
        User user = userRepository.findByAccountId(accId)
                .orElseThrow(UserNotFound::new);

        return UserMapper.toUserRes(user);
    }

    @Override
    public UserRes getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);
        return UserMapper.toUserRes(user);
    }

    @Override
    public Page<UserRes> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toUserRes);
    }

    @Override
    public List<UserRes> getByName(String name) {

        return userRepository.findByName(name)
                .stream()
                .map(UserMapper::toUserRes)
                .toList();
    }

    @Override
    public Boolean isNewBie(UUID id) {

        User u = userRepository.findById(id)
                .orElseThrow(UserNotFound::new);

        return u.getCreatedAt().isAfter(
                Instant.now().
                        minus(90, ChronoUnit.DAYS));
    }

    @Override
    public Boolean rankIsValid(UUID id, Integer rankId) {
        if (rankId == null) {
            return false;
        }

        return userRepository.checkRank(id, rankId);
    }

}
