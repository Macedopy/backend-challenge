package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

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
