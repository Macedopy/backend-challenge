package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotNull
import com.restaurant.pos.orderservice.order.enums.OrderStatus

@Canonical
class OrderStatusUpdateRequest {
    @NotNull
    OrderStatus status
}
