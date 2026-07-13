package com.dev.dungcony.modules.product.services.impl.comment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dungcony.modules.product.dtos.res.CommentRes;
import com.dev.dungcony.modules.product.entities.Comment;
import com.dev.dungcony.modules.product.entities.CommentId;
import com.dev.dungcony.modules.product.exceptions.NotFoundComment;
import com.dev.dungcony.modules.product.mappers.CommentMapper;
import com.dev.dungcony.modules.product.repositories.CommentRepository;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentGetService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentGetImpl implements CommentGetService {

    private final CommentRepository commentRepository;

    private final ProductGetService productGetService;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentRes> getByUId(UUID uid, Pageable pageable) {

        Page<Comment> comments = commentRepository.findUserList(uid, pageable);
        return comments.map(comment -> CommentMapper.toCommentRes(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentRes> getByProductCode(String productCode, Pageable pageable) {
        int productId = productGetService.getIdByCode(productCode);

        Page<Comment> comments = commentRepository.findProductList(productId, pageable);
        return comments.map(comment -> CommentMapper.toCommentRes(comment));

    }

    @Override
    @Transactional(readOnly = true)
    public CommentRes getByProductCodeUid(String productCode, UUID uid) {
        CommentId id = new CommentId(productGetService.getIdByCode(productCode), uid);

        Comment comment = commentRepository.findByIdWithProduct(id)
                .orElseThrow(NotFoundComment::new);

        return CommentMapper.toCommentRes(comment);
    }

}
