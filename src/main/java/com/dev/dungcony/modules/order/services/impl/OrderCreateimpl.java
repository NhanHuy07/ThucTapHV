package com.dev.dungcony.modules.order.services.impl;

import com.dev.dungcony.modules.cart.dtos.CartItemConsumeDto;
import com.dev.dungcony.modules.cart.services.interfaces.CartItemGetService;
import com.dev.dungcony.modules.cart.services.interfaces.CartUpdateService;
import com.dev.dungcony.modules.notifications.services.interfaces.NotificationCreateService;
import com.dev.dungcony.modules.order.dtos.OrderItemDto;
import com.dev.dungcony.modules.order.dtos.req.CreateOrderReq;
import com.dev.dungcony.modules.order.dtos.res.OrderRes;
import com.dev.dungcony.modules.order.entities.Order;
import com.dev.dungcony.modules.order.entities.OrderItem;
import com.dev.dungcony.modules.order.entities.OrderItemId;
import com.dev.dungcony.modules.order.enums.PaymentType;
import com.dev.dungcony.modules.order.exceptions.OrderCannotCreateException;
import com.dev.dungcony.modules.order.exceptions.OrderConflictException;
import com.dev.dungcony.modules.order.mappers.OrderMapper;
import com.dev.dungcony.modules.order.repositories.OrderRepository;
import com.dev.dungcony.modules.order.services.interfaces.OrderCreateService;
import com.dev.dungcony.modules.payment.dtos.res.PaymentRes;
import com.dev.dungcony.modules.payment.services.interfaces.PayOsService;
import com.dev.dungcony.modules.product.dtos.ProductDto;
import com.dev.dungcony.modules.product.services.interfaces.SizeCacheService;
import com.dev.dungcony.modules.product.services.interfaces.item.ItemUpdateService;
import com.dev.dungcony.modules.product.services.interfaces.product.ProductGetService;
import com.dev.dungcony.modules.users.dto.res.ReceiverRes;
import com.dev.dungcony.modules.users.services.interfaces.receivers.RecieverGetService;
import com.dev.dungcony.modules.users.services.interfaces.users.UserUpdateService;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherGetService;
import com.dev.dungcony.modules.voucher.services.interfaces.UserVoucherUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreateimpl implements OrderCreateService {

    private final OrderRepository orderRepo;

    private final ProductGetService productGetService;
    private final SizeCacheService sizeCacheService;
    private final RecieverGetService recieverGetService;
    //USER
    private final UserVoucherGetService userVoucherService;
    private final UserVoucherUpdateService userVoucherUpdateService;
    private final UserUpdateService userUpdateService;
    //CART
    private final CartItemGetService cartItemGetService;
    private final CartUpdateService cartUpdateService;
    private final ItemUpdateService itemUpdateService;

    private final NotificationCreateService notificationCreateService;
    private final PayOsService payOsService;

    @Override
    @Transactional
    public OrderRes userCreateOrder(UUID userId, CreateOrderReq req, String ipAddress) {

        if (req.items() == null || req.items().isEmpty())
            throw new OrderCannotCreateException();

        ReceiverRes receiver = recieverGetService.getReceiverById(userId, req.recieverid());

        // ----------- set Order -----------//
        Order order = OrderMapper.toEnity(userId, req);

        List<String> productCodes = req.items().stream()
                .map(OrderItemDto::productCode)
                .distinct()
                .toList();

        if (!cartItemGetService.existsLitProductCode(userId, productCodes)) {
            throw new OrderCannotCreateException("CONFLICT_PRODUCT_CODE",
                    "prouduct phải thuộc giỏ hàng");
        }


        // lấy thông tin chuẩn của từng sản phẩm
        Map<String, ProductDto> products = productGetService.getDtoByCodes(productCodes);

        // tính chi tiết giá cả
        OrderItemDetail orderItemDetail = new OrderItemDetail(products, req.items(), order);
        orderItemDetail.caculFinalPriceAndVoucherDiscount(userId, req.voucherCode(), req);

        // set lại các thông tin cốt lõi
        orderItemDetail.setOrder(order);

        orderRepo.save(order);


        // giảm số lượng sản phẩm
        reduceProductQuantity(orderItemDetail.cartItemDtos);
        cartUpdateService.consumeListItem(userId, orderItemDetail.cartItemDtos);
        log.info("Order created: {} for user: {}", order.getCode(), userId);
        notificationCreateService.userCreateOrder(userId);

        String paymentUrl = null;
        if (req.paymentType() == PaymentType.ONLINE) {
            PaymentRes paymentRes = payOsService.createPaymentUrl(userId, order.getCode(), ipAddress);
            paymentUrl = paymentRes.paymentUrl();
        } else userUpdateService.increasePurchaseAndUpdateRank(userId, order.getFinalPrice());

        return OrderMapper.toOrderRes(order, orderItemDetail.orderItemDtos, receiver, paymentUrl);
    }

    @Override
    public OrderRes adminCreateOrder(UUID uId, CreateOrderReq req, String ipAddress) {
        return null;
    }

    // -----------------------------PRIVATE-----------------------------------//

    private void reduceProductQuantity(List<CartItemConsumeDto> cartItemDtos) {
        for (CartItemConsumeDto cartItemDto : cartItemDtos) {
            int sizeId = sizeCacheService.getIdBySize(cartItemDto.productSize());
            itemUpdateService.reduce(cartItemDto.productId(), sizeId, cartItemDto.quantity());
        }
    }

    // kiểm tra lại dữ liệu với dữ liệu client gửi lên
    private void validateClientTotals(
            CreateOrderReq req,
            BigDecimal subtotalAmount,
            BigDecimal voucherDiscount,
            BigDecimal finalAmount) {
        if (req.totalPrice() != null && req.totalPrice().compareTo(subtotalAmount) != 0)
            throw new OrderConflictException("Order total price has changed");

        if (req.voucherDiscount() != null && req.voucherDiscount().compareTo(voucherDiscount) != 0)
            throw new OrderConflictException("Voucher discount has changed");

        if (req.finalPrice() != null && req.finalPrice().compareTo(finalAmount) != 0)
            throw new OrderConflictException("Order final price has changed");
    }

    // ----------------------------------------------- INNER CLASS ------------------------------------//
    private class OrderItemDetail {
        List<OrderItemDto> orderItemDtos;
        BigDecimal totalPrice;
        BigDecimal finalPrice;
        BigDecimal voucherDiscount;

        List<CartItemConsumeDto> cartItemDtos;

        public OrderItemDetail(Map<String, ProductDto> products,
                               List<OrderItemDto> itemDtos,
                               Order order) {
            this.totalPrice = BigDecimal.ZERO;
            this.voucherDiscount = BigDecimal.ZERO;
            this.orderItemDtos = new ArrayList<>();
            this.cartItemDtos = new ArrayList<>();

            for (OrderItemDto itemDto : itemDtos) {
                if (itemDto.quantity() <= 0)
                    throw new OrderCannotCreateException(
                            "ITEM_QUANTITY_IS_ZERO",
                            "số lượng sản phẩm phải > 0");

                int sizeId = sizeCacheService.getIdBySize(itemDto.size());
                ProductDto productDto = products.get(itemDto.productCode());
                OrderItem orderItem = getOrderItem(itemDto, productDto, sizeId);

                order.addItem(orderItem);

                totalPrice = totalPrice.add(orderItem.getTotalPrice());
                this.orderItemDtos.add(new OrderItemDto(
                        itemDto.productCode(),
                        itemDto.size(),
                        orderItem.getQuantity(),
                        orderItem.getOriginalPrice(),
                        orderItem.getFinalPrice()));
                this.cartItemDtos.add(new CartItemConsumeDto(
                        productDto.id(),
                        itemDto.size(),
                        itemDto.quantity()
                ));
            }
        }

        public void caculFinalPriceAndVoucherDiscount(UUID userId, String voucherCode, CreateOrderReq req) {
            this.finalPrice = this.totalPrice;

            if (voucherCode != null) {
                this.finalPrice = userVoucherService.applyVoucher(voucherCode, userId, this.totalPrice);
                userVoucherUpdateService.apllyVoucherComplete(userId, voucherCode);
                log.info("có voucher: {}", voucherCode);
            } else {
                log.info("không có voucher");
            }
            this.voucherDiscount = totalPrice.subtract(finalPrice);

            // xác nhận thông tin là đúng
            validateClientTotals(req, this.totalPrice, this.voucherDiscount, this.finalPrice);

        }

        public void setOrder(Order order) {
            order.setTotalPrice(this.totalPrice);
            order.setFinalPrice(this.finalPrice);
            order.setVoucherDiscount(this.voucherDiscount);
        }

        // ---------------------- private function ---------------------------------//

        // láy thông tin order Item
        private OrderItem getOrderItem(OrderItemDto itemDto,
                                       ProductDto product,
                                       int sizeId) {
            if (itemDto.originalPrice() == null) {
                throw new OrderCannotCreateException("PRICE_IS_REQUIRE", "giá sản phẩm phải có");
            }

            if (itemDto.originalPrice().compareTo(product.originalPrice()) != 0 ||
                    itemDto.finalPrice() != null && itemDto.finalPrice().compareTo(product.finalPrice()) != 0) {
                throw new OrderCannotCreateException(
                        "INPUT_ERROR",
                        "thông tin sản phẩm không đúng");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setId(new OrderItemId(null, product.id(), sizeId));
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setOriginalPrice(product.originalPrice());
            orderItem.setFinalPrice(product.finalPrice());
            orderItem.setTotalPrice(product.finalPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));
            return orderItem;
        }

    }
}
