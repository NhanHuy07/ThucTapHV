package com.dev.dungcony.modules.users.repositories;

import com.dev.dungcony.modules.users.entities.Receiver;
import com.dev.dungcony.modules.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RecieverRepository extends JpaRepository<Receiver, Integer> {
    List<Receiver> findAllByUser(User u);

    @Modifying
    @Transactional
    @Query("delete from Receiver r where r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
