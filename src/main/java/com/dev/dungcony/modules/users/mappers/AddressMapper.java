package com.dev.dungcony.modules.users.mappers;

import com.dev.dungcony.modules.users.dto.res.AddressRes;
import com.dev.dungcony.modules.users.entities.Address;

public final class AddressMapper {

    public static AddressRes toDto(Address address) {
        return new AddressRes(
                address.getCountry(),
                address.getProvince(),
                address.getDistrict(),
                address.getStreet(),
                address.getDetail());
    }

    public static Address toEntity(AddressRes dto) {
        Address address = new Address();
        address.setCountry(dto.country());
        address.setProvince(dto.province());
        address.setDistrict(dto.district());
        address.setStreet(dto.street());
        address.setDetail(dto.detail());
        return address;
    }
}