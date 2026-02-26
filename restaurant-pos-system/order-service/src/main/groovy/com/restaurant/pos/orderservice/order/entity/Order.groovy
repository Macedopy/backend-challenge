package com.restaurant.pos.orderservice.order

import groovy.transform.Canonical
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Document(collection = "orders")
@Canonical
class Order {
    @Id
    String id

    CustomerDto customer
    List<OrderItem> orderItems = []

    BigDecimal totalAmount

    OrderStatus status = OrderStatus.CREATED

    @CreatedDate
    LocalDateTime createdAt

    @LastModifiedDate
    LocalDateTime updatedAt
}
