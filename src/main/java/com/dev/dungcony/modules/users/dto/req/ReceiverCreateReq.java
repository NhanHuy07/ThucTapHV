package com.dev.dungcony.modules.users.dto.req;

import com.dev.dungcony.modules.users.dto.res.AddressRes;

public record ReceiverCreateReq(
        String fName,
        String lName,
        String phone,
        AddressRes addr

) {
}
