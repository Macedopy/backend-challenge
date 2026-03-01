package com.restaurant.pos.orderservice.order.controller

import com.restaurant.pos.orderservice.order.OrderController
import com.restaurant.pos.orderservice.order.OrderService
import com.restaurant.pos.orderservice.order.dto.OrderRequest
import com.restaurant.pos.orderservice.order.dto.OrderResponse
import com.restaurant.pos.orderservice.order.dto.OrderStatusUpdateRequest
import com.restaurant.pos.orderservice.order.dto.CustomerDto
import com.restaurant.pos.orderservice.order.dto.OrderItemRequest
import com.restaurant.pos.orderservice.order.dto.OrderItemResponse
import com.restaurant.pos.orderservice.order.enums.OrderStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderControllerTest extends Specification {

    MockMvc mockMvc
    OrderService orderService = Mock(OrderService)
    OrderController orderController = new OrderController(orderService)

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build()
    }

    def "should create order successfully"() {
        given:
        def orderRequest = new OrderRequest(
            customer: new CustomerDto(fullName: "John Doe", address: "123 Main St", email: "john@example.com"),
            orderItems: [new OrderItemRequest(productId: "menu1", quantity: 2)]
        )
        def orderResponse = new OrderResponse(
            id: "order123",
            customer: orderRequest.customer,
            orderItems: [new OrderItemResponse(productId: "menu1", name: "Pizza", quantity: 2, price: new BigDecimal("15.0"))],
            totalAmount: new BigDecimal("30.0"),
            status: OrderStatus.CREATED,
            createdAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            updatedAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        orderService.create(orderRequest) >> orderResponse

        when:
        def result = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/orders")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(orderRequest))
        )

        then:
        result.andReturn().response.status == 201
    }

    def "should update order status successfully"() {
        given:
        def id = "order123"
        // use a valid status from OrderStatus enum (CONFIRMED doesn't exist in order-service)
        def updateRequest = new OrderStatusUpdateRequest(status: OrderStatus.PREPARING)
        def orderResponse = new OrderResponse(
            id: id,
            customer: new CustomerDto(fullName: "John Doe", address: "123 Main St", email: "john@example.com"),
            orderItems: [],
            totalAmount: new BigDecimal("30.0"),
            status: OrderStatus.PREPARING,
            createdAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            updatedAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        orderService.updateStatus(id, updateRequest) >> orderResponse

        when:
        def result = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/orders/${id}/status")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateRequest))
        )

        then:
        result.andReturn().response.status == 200
    }

    def "should find order by ID"() {
        given:
        def id = "order123"
        def orderResponse = new OrderResponse(
            id: id,
            customer: new CustomerDto(fullName: "John Doe", address: "123 Main St", email: "john@example.com"),
            orderItems: [],
            totalAmount: new BigDecimal("30.0"),
            status: OrderStatus.CREATED,
            createdAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            updatedAt: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        orderService.findById(id) >> orderResponse

        when:
        def result = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/orders/${id}")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        )

        then:
        result.andReturn().response.status == 200
    }

    def "should find all orders with pagination"() {
        given:
        def response = [
            orders: [],
            limit: 10,
            offset: 0,
            totalRecords: 0
        ]
        orderService.findAll(10, 0) >> response

        when:
        def result = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/orders?limit=10&offset=0")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        )

        then:
        result.andReturn().response.status == 200
    }
}
