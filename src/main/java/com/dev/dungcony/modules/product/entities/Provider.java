package com.dev.dungcony.modules.product.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.product.enums.ProviderStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "tbl_providers")
public class Provider extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 10)
    @NotNull
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 15)
    @Column(name = "phone", length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ProviderStatus status = ProviderStatus.ACTIVE;

    @Size(max = 255)
    @Column(name = "logo")
    private String logo;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

}