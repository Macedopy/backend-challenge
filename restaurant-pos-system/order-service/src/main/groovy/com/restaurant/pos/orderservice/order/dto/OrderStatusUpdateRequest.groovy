package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotNull

@Canonical
class OrderStatusUpdateRequest {
    @NotNull
    OrderStatus status
}
