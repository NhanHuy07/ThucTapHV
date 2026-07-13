package com.dev.dungcony.modules.order.services.interfaces;

import com.dev.dungcony.modules.order.dtos.req.CreateOrderReq;
import com.dev.dungcony.modules.order.dtos.res.OrderRes;

import java.util.UUID;

public interface OrderCreateService {
    OrderRes userCreateOrder(UUID uId, CreateOrderReq req, String ipAddress);

    OrderRes adminCreateOrder(UUID uId, CreateOrderReq req, String ipAddress);
}
