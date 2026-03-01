package com.restaurant.pos.menuservice.menu.exception

class MenuItemNotFoundException extends RuntimeException {
    MenuItemNotFoundException(String id) {
        super(createMessage(id))
    }

    private static String createMessage(String id) {
        "Menu item not found with id: ${id ?: 'null'}"
    }
}
