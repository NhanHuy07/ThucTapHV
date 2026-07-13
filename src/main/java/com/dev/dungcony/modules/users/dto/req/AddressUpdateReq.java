package com.dev.dungcony.modules.users.dto.req;

public record AddressUpdateReq(
        String country,
        String province,
        String district,
        String street,
        String detail) {

}
