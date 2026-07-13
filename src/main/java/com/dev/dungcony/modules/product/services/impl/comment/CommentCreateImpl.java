package com.dev.dungcony.modules.product.services.impl.comment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.req.CommentAddReq;
import com.dev.dungcony.modules.product.entities.Comment;
import com.dev.dungcony.modules.product.entities.CommentId;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.exceptions.CommentConflictException;
import com.dev.dungcony.modules.product.exceptions.CommentForbiddenException;
import com.dev.dungcony.modules.product.exceptions.CommentInvalidException;
import com.dev.dungcony.modules.product.repositories.CommentRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentCreateService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;
import com.dev.dungcony.modules.order.enums.OrderStatus;
import com.dev.dungcony.modules.order.repositories.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentCreateImpl implements CommentCreateService {

    private final CommentRepository commentRepository;
    private final ProductGetService productGetService;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public String createComment(UUID uId, CommentAddReq req) {
        validate(req);

        Integer productId = productGetService.getIdByCode(req.productCode());
        ensureUserCanComment(uId, productId);

        CommentId id = new CommentId(productId, uId);
        if (commentRepository.existsById(id)) {
            throw new CommentConflictException("Bạn đã bình luận sản phẩm này");
        }

        Comment comment = new Comment();
        comment.setId(id);
        comment.setProduct(entityManager.getReference(Product.class, productId));
        comment.setContent(req.content().trim());
        comment.setRating(req.rating());

        commentRepository.save(comment);
        syncProductRating(productId);

        return "comment" + "/" + uId + "/" + productId;
    }

    private void validate(CommentAddReq req) {
        if (req == null) {
            throw new CommentInvalidException("Thiếu dữ liệu bình luận");
        }
        if (req.productCode() == null || req.productCode().isBlank()) {
            throw new CommentInvalidException("Thiếu mã sản phẩm");
        }
        if (req.content() == null || req.content().isBlank()) {
            throw new CommentInvalidException("Nội dung bình luận không được để trống");
        }
        if (req.rating() == null || req.rating().isNaN() || req.rating() < 0 || req.rating() > 5) {
            throw new CommentInvalidException("Rating phải nằm trong khoảng từ 0 đến 5");
        }
    }

    private void ensureUserCanComment(UUID userId, Integer productId) {
        boolean purchased = orderItemRepository.existsByUserIdAndProductIdAndOrderStatus(
                userId,
                productId,
                OrderStatus.COMPLETED);

        if (!purchased) {
            throw new CommentForbiddenException("Chỉ được bình luận sản phẩm đã nhận thành công");
        }
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
