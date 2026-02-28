package com.restaurant.pos.menuservice.menu.dto

import groovy.transform.Canonical

@Canonical
class MenuItemCreateResponse {
    String id
    String name
    String description
    BigDecimal price
    String createdAt
}

@Canonical
class MenuItemUpdateResponse {
    String id
    String name
    String description
    BigDecimal price
    String updatedAt
}
