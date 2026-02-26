package com.restaurant.pos.menuservice.menu

import com.restaurant.pos.menuservice.menu.entity.MenuItem
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuItemRepository extends MongoRepository<MenuItem, String> {
}
