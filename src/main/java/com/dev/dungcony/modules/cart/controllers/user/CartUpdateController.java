package com.dev.dungcony.modules.cart.controllers.user;

import com.dev.dungcony.modules.cart.dtos.req.RemoveListItemReq;
import com.dev.dungcony.modules.cart.services.interfaces.CartUpdateService;
import com.dev.dungcony.modules.product.enums.ProductSize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.cart.dtos.req.AddToCartReq;
import com.dev.dungcony.modules.cart.dtos.req.UpdateCartItemReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/cart")
@Tag(name = "Carts")
public class CartUpdateController {

    private final CartUpdateService cartUpdateService;

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    @PostMapping("/add")
    public ResponseEntity<ApiRes<Void>> addToCart(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody AddToCartReq req) {
        cartUpdateService.addItemToCart(account.requireUserUuid(), req);
        return ResponseEntity.ok(ApiRes.success("Item added to cart"));
    }

    @Operation(summary = "Cập nhật số lượng sản phẩm")
    @PutMapping("/update-quantity")
    public ResponseEntity<ApiRes<Void>> updateQuantity(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody UpdateCartItemReq req) {
        cartUpdateService.updateItemQuantity(account.requireUserUuid(), req);
        return ResponseEntity.ok(ApiRes.success("Quantity updated"));
    }

    @Operation(summary = "Xóa sản phẩm khỏi giỏ hàng")
    @DeleteMapping("/remove/{product-code}/{size}")
    public ResponseEntity<ApiRes<Void>> removeItem(
            @AuthenticationPrincipal AccountDetails account,
            @PathVariable("product-code") String productCode,
            @PathVariable ProductSize size) {
        cartUpdateService.removeItemFromCart(account.requireUserUuid(), productCode, size);
        return ResponseEntity.ok(ApiRes.success("Item removed from cart"));
    }

    @Operation(summary = "Xóa danh sách sản phẩm khỏi giỏ hàng")
    @DeleteMapping("/removes")
    public ResponseEntity<ApiRes<Void>> removeListItem(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody RemoveListItemReq listItemReq) {

        cartUpdateService.removeListItem(account.requireUserUuid(), listItemReq.listRemove());

        return ResponseEntity.ok(ApiRes.success("Items removed from cart"));
    }

    @Operation(summary = "Xóa toàn bộ giỏ hàng")
    @DeleteMapping("/clear")
    public ResponseEntity<ApiRes<Void>> clearCart(
            @AuthenticationPrincipal AccountDetails account) {
        cartUpdateService.clearCart(account.requireUserUuid());
        return ResponseEntity.ok(ApiRes.success("Cart cleared"));
    }
}
