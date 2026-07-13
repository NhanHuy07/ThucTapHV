package com.dev.dungcony.modules.notifications.repositories;

import com.dev.dungcony.modules.notifications.dtos.res.NotificationRes;
import com.dev.dungcony.modules.notifications.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByCode(String code);

    long countByReceiverIdAndForAdminFalseAndReadedFalseAndIsDeleteFalse(UUID receiverId);

    long countByForAdminTrueAndReadedFalseAndIsDeleteFalse();

    // ==================================== QUERY =================================//

    @Modifying
    @Transactional
    @Query(
            """
                    UPDATE Notification n
                    SET n.isDelete = true
                    WHERE n.isDelete = false
                          AND n.forAdmin = false
                          AND n.receiverId = :receiver_id
                          AND n.code IN (:codes)
                    """
    )
    int deleteByListCodesAndReceiver(
            @Param("codes") List<String> codes,
            @Param("receiver_id") UUID receiverId);

    @Modifying
    @Transactional
    @Query(
            """
                    UPDATE Notification n
                    SET n.isDelete = true
                    WHERE n.isDelete = false
                          AND n.forAdmin = true
                          AND n.code IN (:codes)
                    """
    )
    int deleteByListCodesForAdmin(
            @Param("codes") List<String> codes);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isDelete = true
            WHERE n.isDelete = false
                  AND n.forAdmin = false
                  AND n.receiverId = :receiver_id
            """)
    int clearByReceiver(@Param("receiver_id") UUID receiverId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isDelete = true
            WHERE n.isDelete = false
                  AND n.forAdmin = true
            """)
    int clearByAdmin();

    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            LEFT JOIN User u ON u.id = n.senderId
            WHERE n.isDelete = false
                  AND n.receiverId = :receiver_id
                  AND n.forAdmin = false
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllByReceiver(
            @Param("receiver_id") UUID receiverId,
            Pageable pageable
    );


    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            left join User u on u.id = n.senderId
            WHERE n.isDelete = false AND n.forAdmin = true
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllForAdmin(
            Pageable pageable
    );

    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            left join User u on u.id = n.senderId
            WHERE n.isDelete = false AND n.forAdmin = true and n.readed = false
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllForAdminUnread(
            Pageable pageable
    );

    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            left join User u on u.id = n.senderId
            WHERE n.isDelete = false AND n.forAdmin = true and n.readed = true
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllForAdminReaded(
            Pageable pageable
    );

    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            left join User u on u.id = n.senderId
            WHERE n.isDelete = false AND n.forAdmin = false 
                        and n.readed = false
                        and n.receiverId = :receiver_id
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllForReciverUnread(
            @Param("receiver_id") UUID receiverId,
            Pageable pageable
    );

    @Query("""
            SELECT new com.dev.dungcony.modules.notifications.dtos.res.NotificationRes(
                n.code,
                u.firstName,
                null,
                n.title,
                n.message
            )
            FROM Notification n
            left join User u on u.id = n.senderId
            WHERE n.isDelete = false
                        AND n.forAdmin = false
                        and n.readed = true
                        and n.receiverId = :receiver_id
            ORDER BY n.createdAt DESC
            """)
    Page<NotificationRes> findAllForReceiverReaded(
            @Param("receiver_id") UUID receiverId,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.readed = true
            WHERE n.isDelete = false AND n.forAdmin = true
            """)
    long updateReadAllForAdmin();

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.readed = true
            WHERE n.isDelete = false
                  AND n.forAdmin = false
                  AND n.receiverId = :receiver_id
            """)
    long updateReadAllUser(
            @Param("receiver_id") UUID receiverId
    );
}
