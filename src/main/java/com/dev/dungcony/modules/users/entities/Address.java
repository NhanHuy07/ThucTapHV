package com.dev.dungcony.modules.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Embeddable
public class Address {

    @NotBlank(message = "country not null")
    @Column(name = "country", nullable = false, length = 20)
    private String country;

    @NotBlank(message = "province not null")
    @Column(name = "province", nullable = false, length = 20)
    private String province;

    @NotBlank(message = "district not null")
    @Column(name = "district", nullable = false, length = 20)
    private String district;

    @NotBlank(message = "street not null")
    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Column(name = "detail", length = 255)
    private String detail;
}
