package com.restaurant.pos.orderservice.order.dto

import groovy.transform.Canonical

@Canonical
class CustomerDto {
    String fullName
    String address
    String email
}
