package com.dev.dungcony.modules.product.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.dungcony.modules.product.entities.Comment;
import com.dev.dungcony.modules.product.entities.CommentId;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {

    @Query(
            value = """
                    SELECT c
                    FROM Comment c
                    JOIN FETCH c.product
                    WHERE c.id.productId = :productId
                    ORDER BY c.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(c)
                    FROM Comment c
                    WHERE c.id.productId = :productId
                    """)
    Page<Comment> findProductList(
            @Param("productId") Integer productId,
            Pageable pageable);

    @Query(
            value = """
                    SELECT c
                    FROM Comment c
                    JOIN FETCH c.product
                    WHERE c.id.userId = :userId
                    ORDER BY c.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(c)
                    FROM Comment c
                    WHERE c.id.userId = :userId
                    """)
    Page<Comment> findUserList(
            @Param("userId") UUID userId,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.product
            WHERE c.id = :id
            """)
    java.util.Optional<Comment> findByIdWithProduct(@Param("id") CommentId id);

    @Query("""
            SELECT AVG(c.rating)
            FROM Comment c
            WHERE c.id.productId = :productId
              AND c.rating IS NOT NULL
            """)
    Double findAverageRatingByProductId(@Param("productId") Integer productId);
}
