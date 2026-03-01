package com.restaurant.pos.menuservice.menu.exception

class MenuItemAlreadyExistsException extends RuntimeException {
    MenuItemAlreadyExistsException(String name) {
        super(createMessage(name))
    }

    private static String createMessage(String name) {
        "Menu item already exists with name: ${name ?: 'null'}"
    }
}
