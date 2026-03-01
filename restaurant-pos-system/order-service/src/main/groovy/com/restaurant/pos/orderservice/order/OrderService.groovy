package com.restaurant.pos.orderservice.order

import com.restaurant.pos.orderservice.order.dto.OrderRequest
import com.restaurant.pos.orderservice.order.dto.OrderResponse
import com.restaurant.pos.orderservice.order.dto.OrderItemResponse
import com.restaurant.pos.orderservice.order.dto.OrderStatusUpdateRequest
import com.restaurant.pos.orderservice.order.entity.Order
import com.restaurant.pos.orderservice.order.entity.OrderItem
import com.restaurant.pos.orderservice.order.enums.OrderStatus
import com.restaurant.pos.orderservice.order.integration.MenuItemClient
import com.restaurant.pos.orderservice.shared.config.RabbitMQConfig
import groovy.transform.CompileStatic
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@CompileStatic
class OrderService {

    private final OrderRepository repository
    private final MenuItemClient menuItemClient
    private final RabbitTemplate rabbitTemplate

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    OrderService(OrderRepository repository, MenuItemClient menuItemClient, RabbitTemplate rabbitTemplate) {
        this.repository = repository
        this.menuItemClient = menuItemClient
        this.rabbitTemplate = rabbitTemplate
    }

    OrderResponse create(OrderRequest request) {
        BigDecimal total = BigDecimal.ZERO

        List<OrderItem> items = request.orderItems.collect { reqItem ->
            def menuItem = menuItemClient.getMenuItem(reqItem.productId)
            BigDecimal price = menuItem.price as BigDecimal
            total += price * reqItem.quantity

            new OrderItem(
                    productId: reqItem.productId,
                    name: menuItem.name as String,
                    quantity: reqItem.quantity,
                    price: price
            )
        }

        def order = new Order(
                customer: request.customer,
                orderItems: items,
                totalAmount: total,
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        def saved = repository.save(order)
        toResponse(saved)
    }

    OrderResponse updateStatus(String id, OrderStatusUpdateRequest request) {
        def order = repository.findById(id)
                .orElseThrow { new RuntimeException("Order not found: $id") }

        order.status = request.status
        order.updatedAt = LocalDateTime.now()

        def saved = repository.save(order)

        publishNotification(saved)

        toResponse(saved)
    }

    OrderResponse findById(String id) {
        def order = repository.findById(id)
                .orElseThrow { new RuntimeException("Order not found: $id") }
        toResponse(order)
    }

    Map<String, Object> findAll(int limit, int offset) {
        int pageNumber = offset.intdiv(limit)
        Pageable pageable = PageRequest.of(pageNumber, limit)
        Page<Order> page = repository.findAll(pageable)

        def orders = page.content.collect { toResponse(it) }

        [
                orders: orders,
                limit: limit,
                offset: offset,
                totalRecords: page.totalElements
        ]
    }

    private OrderResponse toResponse(Order order) {
        new OrderResponse(
                id: order.id,
                customer: order.customer,
                orderItems: order.orderItems.collect { new OrderItemResponse(productId: it.productId, name: it.name, quantity: it.quantity, price: it.price) },
                totalAmount: order.totalAmount,
                status: order.status,
                createdAt: order.createdAt?.format(ISO_FORMATTER),
                updatedAt: order.updatedAt?.format(ISO_FORMATTER)
        )
    }

    private void publishNotification(Order order) {
        def message = [
                orderId: order.id,
                status: order.status.name(),
                fullName: order.customer.fullName,
                address: order.customer.address,
                email: order.customer.email
        ]
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_STATUS_QUEUE, message)
    }
}
