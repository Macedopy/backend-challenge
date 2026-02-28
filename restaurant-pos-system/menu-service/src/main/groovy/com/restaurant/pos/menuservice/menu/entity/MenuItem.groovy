package com.restaurant.pos.menuservice.menu.entity

import groovy.transform.Canonical
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.math.BigDecimal
import java.time.LocalDateTime

@Document(collection = "menuItems")
@Canonical
class MenuItem {
    @Id
    String id

    String name
    String description
    BigDecimal price

    LocalDateTime createdAt
    LocalDateTime updatedAt
}
