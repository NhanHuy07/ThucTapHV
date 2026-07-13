package com.dev.dungcony.modules.auth.repositories;

import com.dev.dungcony.modules.auth.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

        /**
         * Tìm account theo email
         */
        Optional<Account> findByEmail(String email);

        Optional<Account> findByUsername(String username);

        /**
         * Kiểm tra email đã tồn tại
         */
        boolean existsByEmail(String email);

        @Modifying
        @Query("""
                        update Account a
                        set a.verify = true
                        where a.email = :email
                        """)
        int verifyEmail(@Param("email") String email);

        @Modifying
        @Query("""
                        update Account a
                        set a.verify = :is_verify
                        where a.id = :acc_id
                        """)
        int updateVerify(
                        @Param("acc_id") Integer accid,
                        @Param("is_verify") boolean isVerify);

}
