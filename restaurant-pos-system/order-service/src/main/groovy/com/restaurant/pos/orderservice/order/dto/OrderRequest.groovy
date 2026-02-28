package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

@Canonical
class OrderRequest {
    CustomerDto customer
    List<OrderItemRequest> orderItems = []
}
