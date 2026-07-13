package com.dev.dungcony.modules.cart.services.impl;

import com.dev.dungcony.modules.cart.dtos.CartItemDto;
import com.dev.dungcony.modules.cart.dtos.CartItemConsumeDto;
import com.dev.dungcony.modules.cart.dtos.req.AddToCartReq;
import com.dev.dungcony.modules.cart.dtos.req.UpdateCartItemReq;
import com.dev.dungcony.modules.cart.entities.CartItem;
import com.dev.dungcony.modules.cart.entities.CartItemId;
import com.dev.dungcony.modules.cart.exceptions.CartItemNotFoundException;
import com.dev.dungcony.modules.cart.exceptions.CartUnProcessableException;
import com.dev.dungcony.modules.cart.exceptions.NotAddOutOfStockProduct;
import com.dev.dungcony.modules.cart.repositories.CartRepository;
import com.dev.dungcony.modules.cart.services.interfaces.CartUpdateService;
import com.dev.dungcony.modules.product.dtos.ProductDto;
import com.dev.dungcony.modules.product.entities.Product;
import com.dev.dungcony.modules.product.entities.Size;
import com.dev.dungcony.modules.product.enums.ProductSize;
import com.dev.dungcony.modules.product.enums.ProductStatus;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;
import com.dev.dungcony.modules.users.entities.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartUpdateImpl implements CartUpdateService {

    private final CartRepository cartItemRepository;
    private final SizeCacheService sizeCacheService;
    private final ProductGetService productGetService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void addItemToCart(UUID userId, AddToCartReq req) {
        ProductDto productDto = productGetService.getDtoByCode(req.productCode());
        Integer sizeId = sizeCacheService.getIdBySize(req.size());

        if (productDto.status() == ProductStatus.OUT_OF_STOCK) {
            throw new NotAddOutOfStockProduct();
        }
        CartItem cartItem = cartItemRepository
                .findById_UserIdAndId_ProductIdAndId_SizeId(userId, productDto.id(), sizeId)
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setId(new CartItemId(userId, productDto.id(), sizeId));
            cartItem.setUser(entityManager.getReference(User.class, userId));
            cartItem.setProduct(entityManager.getReference(Product.class, productDto.id()));
            cartItem.setSize(entityManager.getReference(Size.class, sizeId));
            cartItem.setQuantity(req.quantity());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + req.quantity());
        }

        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeItemFromCart(UUID userId, String productCode, ProductSize size) {
        int productId = productGetService.getIdByCode(productCode);
        int sizeId = sizeCacheService.getIdBySize(size);

        CartItem item = cartItemRepository.findById(new CartItemId(userId, productId, sizeId))
                .orElseThrow(CartItemNotFoundException::new);

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void updateItemQuantity(UUID userId, UpdateCartItemReq req) {
        int productId = productGetService.getIdByCode(req.productCode());
        int sizeId = sizeCacheService.getIdBySize(req.size());

        CartItem cartItem = cartItemRepository.findById(new CartItemId(userId, productId, sizeId))
                .orElseThrow(CartItemNotFoundException::new);

        cartItem.setQuantity(req.quantity());
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeListItem(UUID uid, List<CartItemDto> cartItemDtos) {
        for (CartItemDto cartItemDto : cartItemDtos) {
            int sizeId = sizeCacheService.getIdBySize(cartItemDto.productSize());
            int productId = cartItemDto.productId();
            CartItem item = cartItemRepository.findById(new CartItemId(uid, productId, sizeId))
                    .orElseThrow(CartItemNotFoundException::new);

            cartItemRepository.delete(item);
        }
    }

    @Override
    @Transactional
    public void consumeListItem(UUID uid, List<CartItemConsumeDto> cartItemDtos) {
        for (CartItemConsumeDto cartItemDto : cartItemDtos) {
            if (cartItemDto.quantity() <= 0) {
                throw new CartUnProcessableException();
            }

            int sizeId = sizeCacheService.getIdBySize(cartItemDto.productSize());
            CartItem item = cartItemRepository.findById(new CartItemId(uid, cartItemDto.productId(), sizeId))
                    .orElseThrow(CartItemNotFoundException::new);

            if (item.getQuantity() < cartItemDto.quantity()) {
                throw new CartUnProcessableException();
            }

            int remainingQuantity = item.getQuantity() - cartItemDto.quantity();
            if (remainingQuantity == 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(remainingQuantity);
                cartItemRepository.save(item);
            }
        }
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        List<CartItem> items = cartItemRepository.findAllByUserId(userId);
        cartItemRepository.deleteAllById_UserId(userId);
    }
}
