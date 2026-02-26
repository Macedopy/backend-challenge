package com.restaurant.pos.orderservice.order

import groovy.transform.Canonical

@Canonical
class OrderItem {
    String productId
    String name
    Integer quantity
    BigDecimal price
}
