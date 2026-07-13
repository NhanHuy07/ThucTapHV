package com.dev.dungcony.modules.product.controllers.store;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.product.dtos.res.CommentRes;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentGetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/public/product/comments")
@Tag(name = "Comments")
public class PublicCommentController {

    private final CommentGetService commentGetService;

    @Operation(summary = "Lấy danh sách comment theo sản phẩm")
    @GetMapping
    public ResponseEntity<ApiRes<PageRes<CommentRes>>> getByProductCode(
            @RequestParam("productCode") String productCode,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success(
                        "comments",
                        PageRes.from(commentGetService.getByProductCode(productCode, pageable))));
    }
}
