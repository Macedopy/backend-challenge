package com.restaurant.pos.orderservice.order.exception

class MenuItemUnavailableException extends RuntimeException {
    MenuItemUnavailableException(String productId) {
        super("Menu item unavailable: $productId")
    }
}
