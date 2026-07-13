package com.dev.dungcony.modules.product.services.interfaces.comment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dev.dungcony.modules.product.dtos.res.CommentRes;

public interface CommentGetService {

    Page<CommentRes> getByUId(UUID uid, Pageable pageable);

    Page<CommentRes> getByProductCode(String productCode, Pageable pageable);

    CommentRes getByProductCodeUid(String productCode, UUID uid);

}
