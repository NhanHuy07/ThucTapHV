package com.dev.dungcony.modules.users.services.interfaces.users;

import java.util.List;
import java.util.UUID;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.enums.RankType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserGetService {
    UserRes getUserByAccId(int accId);

    UserRes getUserById(UUID id);

    Page<UserRes> getAll(Pageable pageable);

    List<UserRes> getByName(String name);

    Boolean isNewBie(UUID id);

    Boolean rankIsValid(UUID id, Integer rankId);
}
