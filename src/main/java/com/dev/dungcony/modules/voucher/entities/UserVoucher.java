package com.dev.dungcony.modules.voucher.entities;

import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "tbl_user_vouchers",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_voucher", columnNames = {"user_id", "voucher_id"}))
public class UserVoucher {

    @EmbeddedId
    private UserVoucherId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voucherId")
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    //------------FIELD----------//

    /*
    trạng thái hiện tại của voucher user
    */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserVoucherStatus status = UserVoucherStatus.AVAILABLE;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "min_price_apply")
    private BigDecimal minPriceApply;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

}
