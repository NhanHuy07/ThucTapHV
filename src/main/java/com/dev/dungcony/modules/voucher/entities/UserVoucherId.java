package com.dev.dungcony.modules.voucher.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserVoucherId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "voucher_id")
    private Integer voucherId;

    @Column(name = "user_id")
    private UUID userId;
}
