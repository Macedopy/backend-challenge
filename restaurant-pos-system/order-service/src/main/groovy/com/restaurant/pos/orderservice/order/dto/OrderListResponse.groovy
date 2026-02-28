package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

@Canonical
class OrderListResponse {
    List<OrderResponse> orders = []
    int limit
    int offset
    long totalRecords
}

