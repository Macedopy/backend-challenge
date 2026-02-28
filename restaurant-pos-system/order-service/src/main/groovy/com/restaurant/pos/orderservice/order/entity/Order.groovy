package com.restaurant.pos.orderservice.order.entity

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document

import com.restaurant.pos.orderservice.order.dto.CustomerDto
import com.restaurant.pos.orderservice.order.enums.OrderStatus

import java.time.LocalDateTime

@Document(collection = "orders")
@Canonical
@CompileStatic
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
