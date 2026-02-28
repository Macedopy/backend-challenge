package com.restaurant.pos.menuservice.menu.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Canonical
class MenuItemRequest {
    @NotBlank String name
    String description
    @Positive BigDecimal price
}
