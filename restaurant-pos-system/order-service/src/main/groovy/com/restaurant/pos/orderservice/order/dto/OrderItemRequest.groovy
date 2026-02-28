package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

@Canonical
class OrderItemRequest {
    String productId
    Integer quantity
}
