package com.dev.dungcony.modules.auth.dtos;

import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.auth.enums.Status;

public record AccDto(
        int id,
        String email,
        String username,
        String password,
        Boolean verify,
        Role role,
        Status status
) {
}
