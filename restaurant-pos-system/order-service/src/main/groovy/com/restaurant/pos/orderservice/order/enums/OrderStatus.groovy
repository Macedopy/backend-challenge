package com.restaurant.pos.orderservice.order.enums

import groovy.transform.CompileStatic

@CompileStatic
enum OrderStatus {
    CREATED,
    PREPARING,
    DELIVERED
}
