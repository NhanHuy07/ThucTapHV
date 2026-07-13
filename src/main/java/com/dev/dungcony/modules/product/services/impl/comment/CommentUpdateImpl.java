package com.dev.dungcony.modules.product.services.impl.comment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.req.CommentAddReq;
import com.dev.dungcony.modules.product.dtos.res.CommentRes;
import com.dev.dungcony.modules.product.entities.Comment;
import com.dev.dungcony.modules.product.entities.CommentId;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.exceptions.CommentInvalidException;
import com.dev.dungcony.modules.product.exceptions.NotFoundComment;
import com.dev.dungcony.modules.product.mappers.CommentMapper;
import com.dev.dungcony.modules.product.repositories.CommentRepository;
import com.dev.dungcony.modules.product.repositories.ProductRepository;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentUpdateService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentUpdateImpl implements CommentUpdateService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ProductGetService productGetService;

    @Override
    @Transactional
    public CommentRes updateMyComment(UUID uid, CommentAddReq req) {
        validate(req);

        Integer productId = productGetService.getIdByCode(req.productCode());
        CommentId id = new CommentId(productId, uid);

        Comment comment = commentRepository.findByIdWithProduct(id)
                .orElseThrow(NotFoundComment::new);

        comment.setContent(req.content().trim());
        comment.setRating(req.rating());

        syncProductRating(productId);
        return CommentMapper.toCommentRes(comment);
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
