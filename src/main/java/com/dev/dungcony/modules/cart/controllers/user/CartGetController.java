package com.dev.dungcony.modules.cart.controllers.user;

import com.dev.dungcony.commons.dtos.AccountDetails;
import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.cart.dtos.res.CartRes;
import com.dev.dungcony.modules.cart.services.interfaces.CartGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/cart")
@Tag(name = "Carts")
public class CartGetController {

    private final CartGetService cartGetService;

    @Operation(summary = "Lấy giỏ hàng")
    @GetMapping
    public ResponseEntity<ApiRes<CartRes>> getCart(
            @AuthenticationPrincipal AccountDetails account) {
        return ResponseEntity.ok(
                ApiRes.success("Cart retrieved", cartGetService.getCart(account.requireUserUuid())));
    }
}
