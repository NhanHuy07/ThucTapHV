package com.dev.dungcony.modules.product.controllers.store;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.commons.dtos.PageRes;
import com.dev.dungcony.modules.product.dtos.req.CommentAddReq;
import com.dev.dungcony.modules.product.dtos.res.CommentRes;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentCreateService;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentGetService;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentRemoveService;
import com.dev.dungcony.modules.product.services.interfaces.comment.CommentUpdateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/comment")
@Tag(name = "Comments")
public class UserCommentController {

    private final CommentCreateService commentCreateService;
    private final CommentGetService commentGetService;
    private final CommentUpdateService commentUpdateService;
    private final CommentRemoveService commentRemoveService;

    @Operation(summary = "Tạo comment và rating cho sản phẩm")
    @PostMapping
    public ResponseEntity<ApiRes<String>> createComment(
            @AuthenticationPrincipal AccountDetails account,
            @RequestBody CommentAddReq req) {
        return ResponseEntity.ok(
                ApiRes.success(
                        "comment created",
                        commentCreateService.createComment(account.requireUserUuid(), req)));
    }

    @Operation(summary = "Lấy danh sách comment của tôi")
    @GetMapping("/mine")
    public ResponseEntity<ApiRes<PageRes<CommentRes>>> getMyComments(
            @AuthenticationPrincipal AccountDetails account,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiRes.success(
                        "my comments",
                        PageRes.from(commentGetService.getByUId(account.requireUserUuid(), pageable))));
    }

    @Operation(summary = "Lấy comment của tôi theo sản phẩm")
    @GetMapping("/mine/by-product")
    public ResponseEntity<ApiRes<CommentRes>> getMyCommentByProduct(
            @AuthenticationPrincipal AccountDetails account,
            @RequestParam("productCode") String productCode) {
        return ResponseEntity.ok(
                ApiRes.success(
                        "my comment",
                        commentGetService.getByProductCodeUid(productCode, account.requireUserUuid())));
    }

    @Operation(summary = "Cập nhật comment và rating của tôi")
    @PutMapping("/{productCode}")
    public ResponseEntity<ApiRes<CommentRes>> updateMyComment(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable String productCode,
            @RequestBody CommentAddReq req) {
        CommentAddReq updateReq = new CommentAddReq(productCode, req.content(), req.rating());
        return ResponseEntity.ok(
                ApiRes.success(
                        "comment updated",
                        commentUpdateService.updateMyComment(account.requireUserUuid(), updateReq)));
    }

    @Operation(summary = "Xóa comment của tôi")
    @DeleteMapping("/{productCode}")
    public ResponseEntity<ApiRes<Void>> removeMyComment(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable String productCode) {
        commentRemoveService.removeMyComment(account.requireUserUuid(), productCode);
        return ResponseEntity.ok(ApiRes.success("comment removed"));
    }
}
