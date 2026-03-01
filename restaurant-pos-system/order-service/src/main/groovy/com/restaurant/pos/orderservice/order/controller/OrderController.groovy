package com.restaurant.pos.orderservice.order.controller

import com.restaurant.pos.orderservice.order.dto.OrderRequest
import com.restaurant.pos.orderservice.order.dto.OrderResponse
import com.restaurant.pos.orderservice.order.dto.OrderStatusUpdateRequest
import com.restaurant.pos.orderservice.order.service.OrderService
import groovy.transform.CompileStatic
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@CompileStatic
class OrderController {

    private final OrderService orderService

    OrderController(OrderService orderService) {
        this.orderService = orderService
    }

    @PostMapping
    ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.create(request)
        ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}/status")
    ResponseEntity<OrderResponse> updateStatus(@PathVariable("id") String id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateStatus(id, request)
        ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> findById(@PathVariable("id") String id) {
        OrderResponse response = orderService.findById(id)
        ResponseEntity.ok(response)
    }

    @GetMapping
    ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {
        Map<String, Object> response = orderService.findAll(limit, offset)
        ResponseEntity.ok(response)
    }
}
