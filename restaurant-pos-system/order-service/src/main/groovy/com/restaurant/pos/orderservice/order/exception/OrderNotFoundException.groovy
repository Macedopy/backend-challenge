package com.restaurant.pos.orderservice.order.exception

class OrderNotFoundException extends RuntimeException {
    OrderNotFoundException(String id) {
        super("Order not found: $id")
    }
}
