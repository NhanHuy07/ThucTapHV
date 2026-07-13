package com.dev.dungcony.modules.product.services.impl.comment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.entities.Comment;
import com.dev.dungcony.modules.product.entities.CommentId;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.exceptions.NotFoundComment;
import com.dev.dungcony.modules.product.repositories.CommentRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentRemoveService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentRemoveImpl implements CommentRemoveService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ProductGetService productGetService;

    @Override
    @Transactional
    public void removeMyComment(UUID uid, String productCode) {
        Integer productId = productGetService.getIdByCode(productCode);
        CommentId id = new CommentId(productId, uid);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(NotFoundComment::new);

        commentRepository.delete(comment);
        syncProductRating(productId);
    }

    private void syncProductRating(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found while syncing rating"));

        Double averageRating = commentRepository.findAverageRatingByProductId(productId);
        product.setRated(averageRating == null
                ? null
                : BigDecimal.valueOf(averageRating)
                        .setScale(1, RoundingMode.HALF_UP)
                        .floatValue());
    }
}
