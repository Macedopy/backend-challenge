package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical
import com.restaurant.pos.orderservice.order.enums.OrderStatus

@Canonical
class OrderResponse {
    String id
    CustomerDto customer
    List<OrderItemResponse> orderItems
    BigDecimal totalAmount
    OrderStatus status
    String createdAt
    String updatedAt
}
