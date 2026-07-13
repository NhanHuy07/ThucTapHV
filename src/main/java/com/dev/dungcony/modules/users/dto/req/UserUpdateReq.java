package com.dev.dungcony.modules.users.dto.req;

import java.util.UUID;

public record UserUpdateReq(
        UUID id,
        String firstName,
        String lastName,
        String avatar) {

}
