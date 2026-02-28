package com.restaurant.pos.orderservice.order

import com.restaurant.pos.orderservice.order.entity.Order
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository extends MongoRepository<Order, String> {
}
