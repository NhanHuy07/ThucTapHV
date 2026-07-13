package com.dev.dungcony.modules.users.dto.res;

public record ReceiverRes(
        Integer id,
        AddressRes addr,
        String fName,
        String lName,
        String phone) {
}
