package com.dev.dungcony.modules.product.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.product.entities.Category;
import com.dev.dungcony.modules.product.enums.CategoryStatus;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCode(String categoryCode);

    Optional<Category> findByName(String name);

    List<Category> findByParent_Id(Integer parentId);

    // ------------------- cusstom------------------//
    @Query("""
                SELECT c
                FROM Category c
                WHERE c.path LIKE CONCAT(:path, '/%')
            """)
    List<Category> findAllChildrenByPath(String path);

    @Query("""
                SELECT c
                FROM Category c
                WHERE c.path LIKE CONCAT(
                    (SELECT p.path FROM Category p WHERE p.code = :code),
                    '/%'
                )
            """)
    List<Category> findAllChildrenByCode(String code);

    @Query("""
                SELECT c
                FROM Category c
                WHERE c.isLeaf = true
                AND c.status = :status
                ORDER BY c.level ASC, c.name ASC
            """)
    List<Category> findAllLeafByStatus(@Param("status") CategoryStatus status);

    @Query("""
                SELECT c
                FROM Category c
                WHERE c.path LIKE CONCAT(:pathPrefix, '%')
            """)
    List<Category> findSubTree(String pathPrefix);

    @Modifying
    @Query("""
                UPDATE Category c
                SET c.status = 'HIDDEN'
                WHERE c.path LIKE CONCAT(:pathPrefix, '/%')
                AND c.status = 'ACTIVE'
            """)
    void hideAllByPathPrefix(@Param("pathPrefix") String pathPrefix);


    @Modifying
    @Query("""
            select count(*)
            from Category c
            where c.code in :codes
            """)
    long cntByCodes(@Param("codes") List<String> codes);
}
