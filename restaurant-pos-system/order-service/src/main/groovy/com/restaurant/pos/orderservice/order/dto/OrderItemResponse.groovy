package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

@Canonical
class OrderItemResponse {
    String productId
    String name
    Integer quantity
    BigDecimal price
}
