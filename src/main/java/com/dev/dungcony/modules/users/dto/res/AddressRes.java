package com.dev.dungcony.modules.users.dto.res;

public record AddressRes(
        String country,
        String province,
        String district,
        String street,
        String detail) {
}
