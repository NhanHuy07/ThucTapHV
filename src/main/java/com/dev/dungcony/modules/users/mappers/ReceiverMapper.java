package com.dev.dungcony.modules.users.mappers;

import com.dev.dungcony.modules.users.dto.req.ReceiverCreateReq;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.entities.Receiver;

public final class ReceiverMapper {

    public static ReceiverRes toRes(Receiver receiver) {
        return new ReceiverRes(
                receiver.getId(),
                AddressMapper.toDto(receiver.getAddress()),
                receiver.getFirstName(),
                receiver.getLastName(),
                receiver.getPhone());
    }

    public static Receiver toEntity(ReceiverCreateReq req) {

        Receiver receiver = new Receiver();
        receiver.setPhone(req.phone());
        receiver.setLastName(req.lName());
        receiver.setFirstName(req.fName());
        receiver.setAddress(AddressMapper.toEntity(req.addr()));

        return receiver;
    }

}
