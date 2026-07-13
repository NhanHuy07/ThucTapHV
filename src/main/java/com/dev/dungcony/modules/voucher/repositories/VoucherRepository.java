package com.dev.dungcony.modules.voucher.repositories;

import com.dev.dungcony.modules.voucher.entities.Voucher;
import com.dev.dungcony.modules.voucher.enums.VoucherStatus;
import com.dev.dungcony.modules.voucher.enums.VoucherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Optional<Voucher> findByCode(String code);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Voucher v
            SET v.status = CASE
                WHEN v.endAt IS NOT NULL AND v.endAt <= :now THEN :inactiveStatus
                WHEN v.startAt > :now THEN :commingSoonStatus
                ELSE :activeStatus
            END
            """)
    int checkOrUpdate(
            @Param("now") Instant now,
            @Param("inactiveStatus") VoucherStatus inactiveStatus,
            @Param("commingSoonStatus") VoucherStatus commingSoonStatus,
            @Param("activeStatus") VoucherStatus activeStatus
    );

    boolean existsByCode(String code);

    List<Voucher> findAllByVoucherTypeAndStatus(VoucherType type, VoucherStatus voucherStatus);
}
