package com.dev.dungcony.modules.voucher.repositories;

import com.dev.dungcony.modules.voucher.entities.UserVoucher;
import com.dev.dungcony.modules.voucher.entities.UserVoucherId;
import com.dev.dungcony.modules.voucher.enums.UserVoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, UserVoucherId> {
    boolean existsById(UserVoucherId id);

    @Query("""
            SELECT uv
            FROM UserVoucher uv
            JOIN FETCH uv.voucher
            WHERE uv.id.userId = :userId
            order by uv.endAt desc
            """)
    List<UserVoucher> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT uv
            FROM UserVoucher uv
            JOIN FETCH uv.voucher
            WHERE uv.id.userId = :userId AND uv.status = :status
            order by uv.endAt desc
            """)
    List<UserVoucher> findAllByUserIdByStatus(
            @Param("userId") UUID userId,
            @Param("status") UserVoucherStatus status);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
              UPDATE UserVoucher uv
              SET uv.status = case
                  WHEN uv.endAt <= :now THEN 'EXPIRED'
                  ELSE uv.status
              END
              WHERE uv.status not in :not_check
            """)
    int checkOrUpdate(
            @Param("not_check") List<UserVoucherStatus> notChecks,
            @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO tbl_user_vouchers (voucher_id, user_id, status, end_at, min_price_apply, version)
            SELECT :voucherId, u.id, 'AVAILABLE', :endAt, :minPriceApply, 0
            FROM tbl_users u
            ON CONFLICT (voucher_id, user_id) DO NOTHING
            """, nativeQuery = true)
    int grantGlobalVoucher(
            @Param("voucherId") Integer voucherId,
            @Param("endAt") Instant endAt,
            @Param("minPriceApply") BigDecimal minPriceApply);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO tbl_user_vouchers (voucher_id, user_id, status, end_at, min_price_apply, version)
            SELECT :voucherId, u.id, 'AVAILABLE', :endAt, :minPriceApply, 0
            FROM tbl_users u
            WHERE u.created_at > :createdAfter
            ON CONFLICT (voucher_id, user_id) DO NOTHING
            """, nativeQuery = true)
    int grantNewbieVoucher(
            @Param("voucherId") Integer voucherId,
            @Param("endAt") Instant endAt,
            @Param("minPriceApply") BigDecimal minPriceApply,
            @Param("createdAfter") Instant createdAfter);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO tbl_user_vouchers (voucher_id, user_id, status, end_at, min_price_apply, version)
            SELECT :voucherId, u.id, 'AVAILABLE', :endAt, :minPriceApply, 0
            FROM tbl_users u
            JOIN tbl_ranks user_rank ON user_rank.id = u.rank_id
            JOIN tbl_ranks required_rank ON required_rank.id = :rankId
            WHERE user_rank.level >= required_rank.level
            ON CONFLICT (voucher_id, user_id) DO NOTHING
            """, nativeQuery = true)
    int grantRankVoucher(
            @Param("voucherId") Integer voucherId,
            @Param("rankId") Integer rankId,
            @Param("endAt") Instant endAt,
            @Param("minPriceApply") BigDecimal minPriceApply);

}
