package com.dev.dungcony.modules.users.repositories;

import com.dev.dungcony.modules.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByLastName(String lname);

    Optional<User> findByFirstName(String fname);

    @EntityGraph(attributePaths = "rank")
    Optional<User> findByAccountId(Integer accountId);

    @Override
    @EntityGraph(attributePaths = "rank")
    Optional<User> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = "rank")
    Page<User> findAll(Pageable pageable);

    @Query(value = """
            SELECT * FROM tbl_users u
            WHERE unaccent(LOWER(CONCAT(COALESCE(u.f_name,''), ' ', COALESCE(u.l_name,''))))
                  LIKE unaccent(LOWER(CONCAT('%', :name, '%')))
               OR unaccent(LOWER(CONCAT(COALESCE(u.l_name,''), ' ', COALESCE(u.f_name,''))))
                  LIKE unaccent(LOWER(CONCAT('%', :name, '%')))
            """, nativeQuery = true)
    List<User> findByName(@Param("name") String name);

    @Query("""
                SELECT COUNT(u) > 0
                FROM User u, Rank requiredRank
                WHERE u.id = :uid
                  AND requiredRank.id = :rankId
                  AND u.rank IS NOT NULL
                  AND u.rank.level >= requiredRank.level
            """)
    boolean checkRank(
            @Param("uid") UUID uid,
            @Param("rankId") Integer rankId
    );
}
