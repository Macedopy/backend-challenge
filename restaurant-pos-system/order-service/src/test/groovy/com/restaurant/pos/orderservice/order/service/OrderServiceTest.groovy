package com.restaurant.pos.orderservice.order.service

import com.restaurant.pos.orderservice.order.OrderRepository
import com.restaurant.pos.orderservice.order.OrderService
import com.restaurant.pos.orderservice.order.dto.CustomerDto
import com.restaurant.pos.orderservice.order.dto.OrderItemRequest
import com.restaurant.pos.orderservice.order.dto.OrderRequest
import com.restaurant.pos.orderservice.order.dto.OrderStatusUpdateRequest
import com.restaurant.pos.orderservice.order.entity.Order
import com.restaurant.pos.orderservice.order.entity.OrderItem
import com.restaurant.pos.orderservice.order.enums.OrderStatus
import com.restaurant.pos.orderservice.order.integration.MenuItemClient
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class OrderServiceTest extends Specification {

    OrderRepository orderRepository = Mock(OrderRepository)
    MenuItemClient menuItemClient = Mock(MenuItemClient)
    RabbitTemplate rabbitTemplate = Mock(RabbitTemplate)

    @Subject
    OrderService orderService = new OrderService(orderRepository, menuItemClient, rabbitTemplate)

    def "should create order successfully with items from menu service"() {
        given:
        def customer = new CustomerDto("John Doe", "123 Main St", "john@test.com")
        def orderItem = new OrderItemRequest("product-1", 2)
        def request = new OrderRequest(customer, [orderItem])

        def menuItem = [id: "product-1", name: "Burger", price: 10.50]
        def expectedTotal = new BigDecimal("21.00")

        def savedOrder = new Order(
                id: "order-1",
                customer: customer,
                orderItems: [new OrderItem("product-1", "Burger", 2, new BigDecimal("10.50"))],
                totalAmount: expectedTotal,
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        when:
        menuItemClient.getMenuItem("product-1") >> menuItem
        orderRepository.save(_) >> savedOrder
        def response = orderService.create(request)

        then:
        response.id == "order-1"
        response.customer.fullName == "John Doe"
        response.totalAmount == expectedTotal
        response.status == OrderStatus.CREATED
        response.orderItems.size() == 1
        response.orderItems[0].name == "Burger"
        response.orderItems[0].quantity == 2
    }

    def "should create order with multiple items"() {
        given:
        def customer = new CustomerDto("Jane Smith", "456 Oak Ave", "jane@test.com")
        def orderItem1 = new OrderItemRequest("product-1", 2)
        def orderItem2 = new OrderItemRequest("product-2", 1)
        def request = new OrderRequest(customer, [orderItem1, orderItem2])

        def menuItem1 = [id: "product-1", name: "Burger", price: 10.50]
        def menuItem2 = [id: "product-2", name: "Fries", price: 5.00]
        def expectedTotal = new BigDecimal("26.00")

        def savedOrder = new Order(
                id: "order-2",
                customer: customer,
                orderItems: [
                        new OrderItem("product-1", "Burger", 2, new BigDecimal("10.50")),
                        new OrderItem("product-2", "Fries", 1, new BigDecimal("5.00"))
                ],
                totalAmount: expectedTotal,
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        when:
        menuItemClient.getMenuItem("product-1") >> menuItem1
        menuItemClient.getMenuItem("product-2") >> menuItem2
        orderRepository.save(_) >> savedOrder
        def response = orderService.create(request)

        then:
        response.id == "order-2"
        response.orderItems.size() == 2
        response.totalAmount == expectedTotal
        response.orderItems[0].name == "Burger"
        response.orderItems[1].name == "Fries"
    }

    def "should throw exception when menu item not found"() {
        given:
        def customer = new CustomerDto("John Doe", "123 Main St", "john@test.com")
        def orderItem = new OrderItemRequest("invalid-product", 1)
        def request = new OrderRequest(customer, [orderItem])

        when:
        menuItemClient.getMenuItem("invalid-product") >> { throw new RuntimeException("Menu item not found") }
        orderService.create(request)

        then:
        thrown(RuntimeException)
    }

    def "should update order status successfully"() {
        given:
        def orderId = "order-1"
        def existingOrder = new Order(
                id: orderId,
                customer: new CustomerDto("John Doe", "123 Main St", "john@test.com"),
                orderItems: [new OrderItem("product-1", "Burger", 2, new BigDecimal("10.50"))],
                totalAmount: new BigDecimal("21.00"),
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        def updateRequest = new OrderStatusUpdateRequest(OrderStatus.PREPARING)
        def updatedOrder = new Order(
                id: orderId,
                customer: existingOrder.customer,
                orderItems: existingOrder.orderItems,
                totalAmount: existingOrder.totalAmount,
                status: OrderStatus.PREPARING,
                createdAt: existingOrder.createdAt,
                updatedAt: LocalDateTime.now()
        )

        when:
        orderRepository.findById(orderId) >> Optional.of(existingOrder)
        orderRepository.save(_) >> updatedOrder
        def response = orderService.updateStatus(orderId, updateRequest)

        then:
        response.status == OrderStatus.PREPARING
        1 * rabbitTemplate.convertAndSend(_, _)
    }

    def "should throw exception when order not found for update"() {
        given:
        def orderId = "non-existent-order"
        def updateRequest = new OrderStatusUpdateRequest(OrderStatus.PREPARING)

        when:
        orderRepository.findById(orderId) >> Optional.empty()
        orderService.updateStatus(orderId, updateRequest)

        then:
        thrown(RuntimeException)
    }

    def "should find order by id successfully"() {
        given:
        def orderId = "order-1"
        def order = new Order(
                id: orderId,
                customer: new CustomerDto("John Doe", "123 Main St", "john@test.com"),
                orderItems: [new OrderItem("product-1", "Burger", 2, new BigDecimal("10.50"))],
                totalAmount: new BigDecimal("21.00"),
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        when:
        orderRepository.findById(orderId) >> Optional.of(order)
        def response = orderService.findById(orderId)

        then:
        response.id == orderId
        response.customer.fullName == "John Doe"
        response.status == OrderStatus.CREATED
    }

    def "should throw exception when order not found by id"() {
        given:
        def orderId = "non-existent-order"

        when:
        orderRepository.findById(orderId) >> Optional.empty()
        orderService.findById(orderId)

        then:
        thrown(RuntimeException)
    }

    def "should find all orders with pagination"() {
        given:
        def limit = 10
        def offset = 0
        
        def order1 = new Order(
                id: "order-1",
                customer: new CustomerDto("John Doe", "123 Main St", "john@test.com"),
                orderItems: [new OrderItem("product-1", "Burger", 2, new BigDecimal("10.50"))],
                totalAmount: new BigDecimal("21.00"),
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        def order2 = new Order(
                id: "order-2",
                customer: new CustomerDto("Jane Smith", "456 Oak Ave", "jane@test.com"),
                orderItems: [new OrderItem("product-2", "Fries", 1, new BigDecimal("5.00"))],
                totalAmount: new BigDecimal("5.00"),
                status: OrderStatus.DELIVERED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        def mockPage = Mock(org.springframework.data.domain.Page)

        when:
        orderRepository.findAll(_ as org.springframework.data.domain.Pageable) >> mockPage
        mockPage.getContent() >> [order1, order2]
        mockPage.getTotalElements() >> 2

        def result = orderService.findAll(limit, offset)

        then:
        result.orders.size() == 2
        result.limit == limit
        result.offset == offset
        result.totalRecords == 2
        result.orders[0].id == "order-1"
        result.orders[1].id == "order-2"
    }

    def "should find all orders starting from given offset"() {
        given:
        def limit = 5
        def offset = 5
        
        def order = new Order(
                id: "order-3",
                customer: new CustomerDto("Bob Wilson", "789 Pine St", "bob@test.com"),
                orderItems: [new OrderItem("product-3", "Pizza", 1, new BigDecimal("15.00"))],
                totalAmount: new BigDecimal("15.00"),
                status: OrderStatus.CREATED,
                createdAt: LocalDateTime.now(),
                updatedAt: LocalDateTime.now()
        )

        def mockPage = Mock(org.springframework.data.domain.Page)

        when:
        orderRepository.findAll(_ as org.springframework.data.domain.Pageable) >> mockPage
        mockPage.getContent() >> [order]
        mockPage.getTotalElements() >> 10

        def result = orderService.findAll(limit, offset)

        then:
        result.orders.size() == 1
        result.limit == limit
        result.offset == offset
        result.totalRecords == 10
        result.orders[0].id == "order-3"
    }

    def "should map order with null dates in toResponse"() {
        given:
        def orderId = "order-null-dates"
        def order = new Order(
                id: orderId,
                customer: new CustomerDto("Richard", "123 Main St", "richard@test.com"),
                orderItems: [new OrderItem("product-1", "Burger", 1, new BigDecimal("10.50"))],
                totalAmount: new BigDecimal("10.50"),
                status: OrderStatus.CREATED,
                createdAt: null,
                updatedAt: null
        )

        when:
        orderRepository.findById(orderId) >> Optional.of(order)
        def response = orderService.findById(orderId)

        then:
        response.id == orderId
        response.createdAt == null
        response.updatedAt == null
    }
}
